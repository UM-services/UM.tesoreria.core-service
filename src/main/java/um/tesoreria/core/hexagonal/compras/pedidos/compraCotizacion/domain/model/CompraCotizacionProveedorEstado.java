package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

/**
 * Estados del pedido de cotización dirigido a un proveedor (documento por proveedor).
 *
 * <p>{@code PENDIENTE} mientras el proceso es borrador; {@code ENVIADA} al enviarse el proceso
 * (sin correo real todavía); {@code RESPONDIDA} / {@code NO_COTIZA} los completa la etapa 6C.</p>
 */
public enum CompraCotizacionProveedorEstado {

    PENDIENTE,
    ENVIADA,
    RESPONDIDA,
    NO_COTIZA,
    ANULADA
}
