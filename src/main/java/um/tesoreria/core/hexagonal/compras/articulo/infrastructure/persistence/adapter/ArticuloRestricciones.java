package um.tesoreria.core.hexagonal.compras.articulo.infrastructure.persistence.adapter;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloConflictException;
import um.tesoreria.core.hexagonal.compras.articulo.application.exception.ArticuloValidationException;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ReferenciaArticulo;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * Traduce las violaciones de restricción y los errores de bloqueo de MySQL al escribir {@code articulos}. Distingue por código de error
 * y nombre de restricción (leídos de {@code information_schema} de dev el 2026-10-05), nunca por el texto.
 * Un nombre desconocido recibe la respuesta genérica de su código; cualquier otro error sigue sin traducir (500).
 * Excepción: el 1366 (texto que la columna latin1 no admite) no trae restricción y la columna sale de su mensaje.
 */
@Slf4j
final class ArticuloRestricciones {

    static final int CLAVE_DUPLICADA = 1062;
    static final int ESPERA_DE_BLOQUEO_VENCIDA = 1205;
    static final int INTERBLOQUEO = 1213;
    static final int FK_HIJO_EXISTENTE = 1451;
    static final int FK_PADRE_INEXISTENTE = 1452;
    static final int TEXTO_NO_ADMITIDO = 1366;

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

    static RuntimeException traducir(RuntimeException ex, Long articuloId, String operacion) {
        var codigo = codigoMysql(ex);
        if (codigo == INTERBLOQUEO || codigo == ESPERA_DE_BLOQUEO_VENCIDA) {
            log.warn("Artículo {} ({}): {}", articuloId, operacion, codigo == INTERBLOQUEO ? "interbloqueo" : "espera de bloqueo vencida");
            return ArticuloConflictException.bloqueado(articuloId, codigo == INTERBLOQUEO);
        }
        if (codigo == TEXTO_NO_ADMITIDO) {
            var campo = campoDelTexto(ex);
            log.warn("Artículo {} ({}): la base no admite el texto de {}", articuloId, operacion, campo);
            return new ArticuloValidationException(campo, (campo != null ? campo : "Un texto")
                    + " tiene caracteres que la base no admite (por ejemplo, emojis).");
        }
        if (codigo != CLAVE_DUPLICADA && codigo != FK_HIJO_EXISTENTE && codigo != FK_PADRE_INEXISTENTE) {
            return ex;
        }
        var restriccion = nombreRestriccion(ex);
        return switch (codigo) {
            case CLAVE_DUPLICADA -> "PRIMARY".equals(restriccion)
                    ? conocida(ArticuloConflictException.idDuplicado(articuloId), restriccion, articuloId, operacion)
                    : desconocida(ArticuloConflictException.conflicto(articuloId), codigo, restriccion, articuloId, operacion);
            case FK_PADRE_INEXISTENTE -> restriccion != null && CAMPO_POR_FK.containsKey(restriccion)
                    ? conocida(new ArticuloValidationException(CAMPO_POR_FK.get(restriccion),
                            ArticuloValidationException.CUENTA_INEXISTENTE), restriccion, articuloId, operacion)
                    : desconocida(new ArticuloValidationException(null, "Algún dato referenciado no existe."),
                            codigo, restriccion, articuloId, operacion);
            default -> restriccion != null && TABLA_POR_FK.containsKey(restriccion)
                    ? conocida(ArticuloConflictException.referenciado(articuloId,
                            List.of(new ReferenciaArticulo(TABLA_POR_FK.get(restriccion), null))), restriccion, articuloId, operacion)
                    : desconocida(ArticuloConflictException.referenciado(articuloId, List.of()),
                            codigo, restriccion, articuloId, operacion);
        };
    }

    private static RuntimeException conocida(RuntimeException traducida, String restriccion, Long articuloId, String operacion) {
        // La validación previa no lo frenó: lo frenó la base
        log.warn("Artículo {} ({}): la base rechazó la escritura por {}", articuloId, operacion, restriccion);
        return traducida;
    }

    private static RuntimeException desconocida(RuntimeException traducida, int codigo, String restriccion, Long articuloId, String operacion) {
        log.error("Artículo {} ({}): restricción desconocida {} (error {}); respuesta genérica", articuloId, operacion, restriccion, codigo);
        return traducida;
    }

    /** Campo JSON de la columna que nombra el mensaje del 1366; nulo si no se reconoce. */
    static String campoDelTexto(Throwable ex) {
        for (var t = ex; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql && sql.getMessage() != null) {
                var m = COLUMNA.matcher(sql.getMessage());
                return m.find() ? CAMPO_POR_COLUMNA.get(m.group(1)) : null;
            }
        }
        return null;
    }

    /** Código de error de MySQL de la primera {@link SQLException} de la cadena; 0 si no hay. */
    static int codigoMysql(Throwable ex) {
        for (var t = ex; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql) {
                return sql.getErrorCode();
            }
        }
        return 0;
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
