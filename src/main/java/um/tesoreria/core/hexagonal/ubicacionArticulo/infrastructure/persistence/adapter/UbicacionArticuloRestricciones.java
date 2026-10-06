package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.persistence.adapter;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloConflictException;
import um.tesoreria.core.hexagonal.ubicacionArticulo.application.exception.UbicacionArticuloValidationException;

import java.sql.SQLException;
import java.util.Map;

/**
 * Traduce los errores de MySQL al escribir {@code ubicacion_articulo}. Distingue por código de error y nombre de
 * restricción (leídos de {@code information_schema} de dev el 2026-10-05), nunca por el texto.
 * Un nombre desconocido recibe la respuesta genérica de su código; cualquier otro error sigue sin traducir (500).
 */
@Slf4j
final class UbicacionArticuloRestricciones {

    static final int CLAVE_DUPLICADA = 1062;
    static final int INTERBLOQUEO = 1213;
    static final int ESPERA_DE_BLOQUEO_VENCIDA = 1205;
    static final int FK_HIJO_EXISTENTE = 1451;
    static final int FK_PADRE_INEXISTENTE = 1452;

    /** Índice único (ubicacion_id, articulo_id). */
    static final String UNICO_PAR = "ubicacion_id";
    static final Map<String, String> CAMPO_POR_FK = Map.of(
            "ubicacion_articulo_ibfk_1", "ubicacionId",
            "ubicacion_articulo_ibfk_2", "articuloId",
            "ubicacion_articulo_ibfk_3", "numeroCuenta");
    static final Map<String, String> MENSAJE_POR_CAMPO = Map.of(
            "ubicacionId", "La ubicación indicada no existe.",
            "articuloId", "El artículo indicado no existe.",
            "numeroCuenta", "La cuenta indicada no existe en el plan de cuentas.");

    private UbicacionArticuloRestricciones() {
    }

    static RuntimeException traducir(RuntimeException ex, Integer ubicacionId, Long articuloId) {
        var codigo = codigoMysql(ex);
        var clave = ubicacionId + ":" + articuloId;
        if (codigo == INTERBLOQUEO) {
            return new UbicacionArticuloConflictException(true, "interbloqueo al asignar " + clave);
        }
        if (codigo == ESPERA_DE_BLOQUEO_VENCIDA) {
            // Reintentar sería esperar de nuevo lo mismo: sale como 409
            log.warn("Asignación {}: espera de bloqueo vencida", clave);
            return UbicacionArticuloConflictException.bloqueado("espera de bloqueo vencida al asignar " + clave);
        }
        if (codigo != CLAVE_DUPLICADA && codigo != FK_HIJO_EXISTENTE && codigo != FK_PADRE_INEXISTENTE) {
            return ex;
        }
        var restriccion = nombreRestriccion(ex);
        if (codigo == CLAVE_DUPLICADA && UNICO_PAR.equals(restriccion)) {
            return new UbicacionArticuloConflictException(true, "otra transacción insertó " + clave);
        }
        if (codigo == FK_PADRE_INEXISTENTE && restriccion != null && CAMPO_POR_FK.containsKey(restriccion)) {
            var campo = CAMPO_POR_FK.get(restriccion);
            // La validación previa no lo frenó: lo frenó la base
            log.warn("Asignación {}: la base rechazó la escritura por {}", clave, restriccion);
            return new UbicacionArticuloValidationException(campo, MENSAJE_POR_CAMPO.get(campo));
        }
        log.error("Asignación {}: restricción desconocida {} (error {}); respuesta genérica", clave, restriccion, codigo);
        return codigo == FK_PADRE_INEXISTENTE
                ? new UbicacionArticuloValidationException(null, "Algún dato referenciado no existe.")
                : new UbicacionArticuloConflictException(false, "la asignación " + clave + " choca con otro dato");
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
