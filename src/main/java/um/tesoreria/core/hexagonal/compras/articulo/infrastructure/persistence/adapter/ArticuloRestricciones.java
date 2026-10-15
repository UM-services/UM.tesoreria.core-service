package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Traduce las violaciones de restricción y los errores de bloqueo al escribir {@code articulos}. Distingue por la
 * excepción de Spring que corresponde al error del motor ({@link SQLExceptionTranslator}) y por el tipo y nombre de
 * restricción que extrae el dialecto de Hibernate (nombres leídos de {@code information_schema} de dev el
 * 2026-10-05), nunca por el texto. Un nombre desconocido recibe la respuesta genérica; cualquier otro error sigue
 * sin traducir (500). Excepción: el texto que la columna latin1 no admite (ver {@link #esTextoNoAdmitido}).
 */
@Slf4j
final class ArticuloRestricciones {

    static final String BAJA = "baja";

    static final Map<String, String> CAMPO_POR_COLUMNA = Map.of(
            "Art_Nombre", "nombre",
            "Art_Descripcion", "descripcion",
            "Art_Unidad", "unidad");
    private static final java.util.regex.Pattern COLUMNA = java.util.regex.Pattern.compile("for column '(\\w+)'");

    static final Map<String, String> CAMPO_POR_FK = Map.of("articulos_ibfk_1", "numeroCuenta");
    static final Map<String, String> TABLA_POR_FK = Map.of(
            "entrega_detalle_ibfk_2", "entrega_detalle",
            "ubicacion_articulo_ibfk_2", "ubicacion_articulo");

    private ArticuloRestricciones() {
    }

    static RuntimeException traducir(SQLExceptionTranslator traductor, RuntimeException ex, Long articuloId, String operacion) {
        if (esTextoNoAdmitido(ex)) {
            var campo = campoDelTexto(ex);
            log.warn("Artículo {} ({}): la base no admite el texto de {}", articuloId, operacion, campo);
            return new ArticuloValidationException(campo, (campo != null ? campo : "Un texto")
                    + " tiene caracteres que la base no admite (por ejemplo, emojis).");
        }
        var traducida = comoSpring(traductor, ex, operacion);
        if (traducida instanceof PessimisticLockingFailureException) {
            // Espera vencida: CannotAcquireLockException; interbloqueo: el resto (DeadlockLoserDataAccessException)
            var interbloqueo = !(traducida instanceof CannotAcquireLockException);
            log.warn("Artículo {} ({}): {}", articuloId, operacion, interbloqueo ? "interbloqueo" : "espera de bloqueo vencida");
            return ArticuloConflictException.bloqueado(articuloId, interbloqueo);
        }
        var restriccion = nombreRestriccion(ex);
        if (traducida instanceof DuplicateKeyException) {
            return "PRIMARY".equals(restriccion)
                    ? conocida(ArticuloConflictException.idDuplicado(articuloId), restriccion, articuloId, operacion)
                    : desconocida(ArticuloConflictException.conflicto(articuloId), traducida, restriccion, articuloId, operacion);
        }
        if (!(traducida instanceof DataIntegrityViolationException) || !esClaveForanea(ex)) {
            return ex;
        }
        if (restriccion != null && CAMPO_POR_FK.containsKey(restriccion)) {
            return conocida(new ArticuloValidationException(CAMPO_POR_FK.get(restriccion),
                    ArticuloValidationException.CUENTA_INEXISTENTE), restriccion, articuloId, operacion);
        }
        if (restriccion != null && TABLA_POR_FK.containsKey(restriccion)) {
            return conocida(ArticuloConflictException.referenciado(articuloId,
                    List.of(new ReferenciaArticulo(TABLA_POR_FK.get(restriccion), null))), restriccion, articuloId, operacion);
        }
        // FK desconocida: en la baja la fila está referenciada; en el alta o la edición falta el dato referenciado
        return BAJA.equals(operacion)
                ? desconocida(ArticuloConflictException.referenciado(articuloId, List.of()), traducida, restriccion, articuloId, operacion)
                : desconocida(new ArticuloValidationException(null, "Algún dato referenciado no existe."),
                        traducida, restriccion, articuloId, operacion);
    }

    private static RuntimeException conocida(RuntimeException traducida, String restriccion, Long articuloId, String operacion) {
        // La validación previa no lo frenó: lo frenó la base
        log.warn("Artículo {} ({}): la base rechazó la escritura por {}", articuloId, operacion, restriccion);
        return traducida;
    }

    private static RuntimeException desconocida(RuntimeException traducida, DataAccessException error, String restriccion,
                                                Long articuloId, String operacion) {
        log.error("Artículo {} ({}): restricción desconocida {} ({}); respuesta genérica", articuloId, operacion, restriccion,
                error.getClass().getSimpleName());
        return traducida;
    }

    /**
     * Texto que la columna latin1 no admite (por ejemplo, un emoji). Específico de MySQL: revisar al migrar a Postgres.
     * No hay excepción portable para este error (MySQL lo informa con el SQLState genérico HY000), así que se reconoce
     * por su código y la columna sale del mensaje ({@link #campoDelTexto}).
     */
    static boolean esTextoNoAdmitido(Throwable ex) {
        var sql = sqlException(ex);
        return sql != null && sql.getErrorCode() == 1366;
    }

    /** Campo JSON de la columna que nombra el mensaje del texto no admitido; nulo si no se reconoce. */
    static String campoDelTexto(Throwable ex) {
        var sql = sqlException(ex);
        if (sql == null || sql.getMessage() == null) {
            return null;
        }
        var m = COLUMNA.matcher(sql.getMessage());
        return m.find() ? CAMPO_POR_COLUMNA.get(m.group(1)) : null;
    }

    /** La excepción de Spring para la {@link SQLException} de la cadena; nula si no hay o no se reconoce. */
    static DataAccessException comoSpring(SQLExceptionTranslator traductor, Throwable ex, String operacion) {
        var sql = sqlException(ex);
        return sql == null ? null : traductor.translate(operacion, null, sql);
    }

    private static SQLException sqlException(Throwable ex) {
        for (var t = ex; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql) {
                return sql;
            }
        }
        return null;
    }

    private static boolean esClaveForanea(Throwable ex) {
        for (var t = ex; t != null; t = t.getCause()) {
            if (t instanceof ConstraintViolationException cve) {
                return cve.getKind() == ConstraintViolationException.ConstraintKind.FOREIGN_KEY;
            }
        }
        return false;
    }

    /** Nombre que extrae el dialecto de Hibernate (MySQL 8 lo antepone con la tabla: se deja solo el nombre); nulo si no lo extrajo. */
    static String nombreRestriccion(Throwable ex) {
        for (var t = ex; t != null; t = t.getCause()) {
            if (t instanceof ConstraintViolationException cve && cve.getConstraintName() != null) {
                var nombre = cve.getConstraintName();
                return nombre.substring(nombre.lastIndexOf('.') + 1);
            }
        }
        return null;
    }

}
