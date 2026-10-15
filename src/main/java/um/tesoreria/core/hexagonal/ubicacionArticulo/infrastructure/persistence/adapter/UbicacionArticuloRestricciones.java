package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.jdbc.support.SQLExceptionTranslator;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * Traduce los errores al escribir {@code ubicacion_articulo}. Distingue por la excepción de Spring que corresponde
 * al error del motor ({@link SQLExceptionTranslator}) y por el tipo y nombre de restricción que extrae el dialecto
 * de Hibernate (nombres leídos de {@code information_schema} de dev el 2026-10-05), nunca por el texto.
 * Un nombre desconocido recibe la respuesta genérica; cualquier otro error sigue sin traducir (500).
 */
@Slf4j
final class UbicacionArticuloRestricciones {

    /** Índice único (ubicacion_id, articulo_id). */
    static final String UNICO_PAR = "ubicacion_id";
    static final Map<String, String> CAMPO_POR_FK = Map.of(
            "ubicacion_articulo_ibfk_1", "ubicacionId",
            "ubicacion_articulo_ibfk_2", "articuloId",
            "ubicacion_articulo_ibfk_3", "numeroCuenta");
    static final Map<String, String> MENSAJE_POR_CAMPO = Map.of(
            "ubicacionId", UbicacionArticuloValidationException.UBICACION_INEXISTENTE,
            "articuloId", UbicacionArticuloValidationException.ARTICULO_INEXISTENTE,
            "numeroCuenta", UbicacionArticuloValidationException.CUENTA_INEXISTENTE);

    private UbicacionArticuloRestricciones() {
    }

    /**
     * Baja de los vínculos dentro de la baja del artículo: solo puede chocar por bloqueos (ninguna tabla referencia a
     * {@code ubicacion_articulo}). El resto sigue sin traducir.
     */
    static RuntimeException traducirBaja(SQLExceptionTranslator traductor, RuntimeException ex, Long articuloId) {
        if (!(comoSpring(traductor, ex) instanceof PessimisticLockingFailureException bloqueo)) {
            return ex;
        }
        if (!(bloqueo instanceof CannotAcquireLockException)) {
            return new UbicacionArticuloConflictException(true, "interbloqueo al borrar los vínculos del artículo " + articuloId);
        }
        log.warn("Baja de los vínculos del artículo {}: espera de bloqueo vencida", articuloId);
        return UbicacionArticuloConflictException.bloqueado("espera de bloqueo vencida al borrar los vínculos del artículo " + articuloId);
    }

    static RuntimeException traducir(SQLExceptionTranslator traductor, RuntimeException ex, Integer ubicacionId, Long articuloId) {
        var traducida = comoSpring(traductor, ex);
        var clave = ubicacionId + ":" + articuloId;
        if (traducida instanceof PessimisticLockingFailureException) {
            // Espera vencida: CannotAcquireLockException; interbloqueo: el resto (DeadlockLoserDataAccessException)
            if (!(traducida instanceof CannotAcquireLockException)) {
                return new UbicacionArticuloConflictException(true, "interbloqueo al asignar " + clave);
            }
            // Reintentar sería esperar de nuevo lo mismo: sale como 409
            log.warn("Asignación {}: espera de bloqueo vencida", clave);
            return UbicacionArticuloConflictException.bloqueado("espera de bloqueo vencida al asignar " + clave);
        }
        var restriccion = nombreRestriccion(ex);
        if (traducida instanceof DuplicateKeyException) {
            if (UNICO_PAR.equals(restriccion)) {
                return new UbicacionArticuloConflictException(true, "otra transacción insertó " + clave);
            }
            log.error("Asignación {}: restricción desconocida {} (clave duplicada); respuesta genérica", clave, restriccion);
            return new UbicacionArticuloConflictException(false, "la asignación " + clave + " choca con otro dato");
        }
        if (!(traducida instanceof DataIntegrityViolationException) || !esClaveForanea(ex)) {
            return ex;
        }
        // Ninguna tabla referencia a ubicacion_articulo: una FK rechazada es un dato referenciado que no existe
        if (restriccion != null && CAMPO_POR_FK.containsKey(restriccion)) {
            var campo = CAMPO_POR_FK.get(restriccion);
            // La validación previa no lo frenó: lo frenó la base
            log.warn("Asignación {}: la base rechazó la escritura por {}", clave, restriccion);
            return new UbicacionArticuloValidationException(campo, MENSAJE_POR_CAMPO.get(campo));
        }
        log.error("Asignación {}: restricción desconocida {} (clave foránea); respuesta genérica", clave, restriccion);
        return new UbicacionArticuloValidationException(null, "Algún dato referenciado no existe.");
    }

    /** La excepción de Spring para la {@link SQLException} de la cadena; nula si no hay o no se reconoce. */
    static DataAccessException comoSpring(SQLExceptionTranslator traductor, Throwable ex) {
        for (var t = ex; t != null; t = t.getCause()) {
            if (t instanceof SQLException sql) {
                return traductor.translate("ubicacion_articulo", null, sql);
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
