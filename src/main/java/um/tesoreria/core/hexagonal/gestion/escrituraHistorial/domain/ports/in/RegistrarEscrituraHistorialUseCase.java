package um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.ports.in;

import um.tesoreria.core.hexagonal.gestion.escrituraHistorial.domain.model.EscrituraHistorial;

/**
 * Contrato reutilizable para registrar escrituras de Gestión en la misma
 * transacción de la operación de negocio. Sin consulta pública ni actor verificado.
 *
 * <p>Uso típico desde un servicio de escritura ya anotado con {@code @Transactional}:
 * <pre>
 *   historial.registrarAlta("ejercicio", String.valueOf(id), estadoNuevo);
 *   historial.registrarEdicion("proveedor", String.valueOf(id), antes, despues);
 *   historial.registrarBaja("bancaria", String.valueOf(id), estadoPrevio);
 * </pre>
 *
 * <p>Si la transacción de negocio se revierte, el evento no queda persistido.
 * Sin una transacción activa lanza {@code IllegalTransactionStateException}.
 *
 * <p>Errores ({@code IllegalArgumentException}): entidad o clave en blanco, entidad de más de
 * 128 caracteres o clave de más de 255 (después de recortar espacios), clave {@code "null"},
 * estado obligatorio nulo o un valor que no se puede serializar. Cualquier error marca la
 * transacción del caller como rollback-only: capturarlo no salva la operación de negocio.
 *
 * <p>Los valores se pasan como copia del estado (DTO, record o {@code Map}), no como entidad JPA:
 * el {@code save} del caller pisa la entidad administrada y el historial guardaría el estado nuevo
 * como anterior. El serializer rechaza entidades, proxies lazy y colecciones o mapas de Hibernate en
 * cualquier nivel del valor, también como clave de un {@code Map}; las claves de un {@code Map} solo
 * pueden ser texto, enteros, enums o booleanos. Se guardan como JSON compacto con claves ordenadas y fechas ISO-8601;
 * un {@code String} se guarda como cadena JSON, no como JSON crudo.
 *
 * <p>El evento devuelto no trae {@code fecha}: la asigna MySQL al insertar.
 */
public interface RegistrarEscrituraHistorialUseCase {

    /**
     * Alta: valor anterior NULL; valor nuevo (obligatorio) con el estado creado.
     */
    EscrituraHistorial registrarAlta(String entidad, String entidadClave, Object valorNuevo);

    /**
     * Edición: conserva antes y después (ambos obligatorios).
     */
    EscrituraHistorial registrarEdicion(String entidad, String entidadClave, Object valorAnterior, Object valorNuevo);

    /**
     * Baja: conserva el estado previo (obligatorio); valor nuevo NULL.
     */
    EscrituraHistorial registrarBaja(String entidad, String entidadClave, Object valorAnterior);
}
