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
 */
public interface RegistrarEscrituraHistorialUseCase {

    /**
     * Alta: valor anterior vacío; valor nuevo con el estado creado.
     */
    EscrituraHistorial registrarAlta(String entidad, String entidadClave, Object valorNuevo);

    /**
     * Edición: conserva antes y después.
     */
    EscrituraHistorial registrarEdicion(String entidad, String entidadClave, Object valorAnterior, Object valorNuevo);

    /**
     * Baja: conserva el estado previo; valor nuevo vacío.
     */
    EscrituraHistorial registrarBaja(String entidad, String entidadClave, Object valorAnterior);
}
