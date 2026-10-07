package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

/**
 * Estados del pedido de compra.
 *
 * <p>La edición del pedido está permitida hasta la primera autorización, por eso
 * {@link #esEditable()} incluye los estados previos a {@code AUTORIZADA}.</p>
 */
public enum CompraPedidoEstado {

    BORRADOR,
    PENDIENTE_ESTIMACION,
    PENDIENTE_AUTORIZACION,
    ACLARACION_REQUERIDA,
    AUTORIZADA,
    RECHAZADA,
    ANULADA;

    public boolean esEditable() {
        return this == BORRADOR || this == PENDIENTE_AUTORIZACION || this == ACLARACION_REQUERIDA;
    }

}
