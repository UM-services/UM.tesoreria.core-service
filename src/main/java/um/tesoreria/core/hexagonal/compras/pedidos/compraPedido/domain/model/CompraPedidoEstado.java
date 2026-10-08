package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

/**
 * Estados del pedido de compra.
 *
 * <p>Circuito de envío: el solicitante presenta el pedido ({@code BORRADOR -> PENDIENTE_ENVIO});
 * el autorizante aprueba ({@code -> ENVIADO}, pasa a compras) o rechaza
 * ({@code -> RECHAZADO}, vuelve al solicitante). El solicitante puede corregir y volver a
 * presentar un rechazado, o descartarlo ({@code -> DESCARTADO}).</p>
 *
 * <p>La edición del pedido está permitida mientras no haya pasado a compras: en
 * {@link #BORRADOR}, {@link #RECHAZADO} y los estados previos de la etapa de estimación.</p>
 */
public enum CompraPedidoEstado {

    BORRADOR,
    PENDIENTE_ESTIMACION,
    PENDIENTE_ENVIO,
    ACLARACION_REQUERIDA,
    ENVIADO,
    RECHAZADO,
    DESCARTADO;

    public boolean esEditable() {
        return this == BORRADOR || this == RECHAZADO
                || this == PENDIENTE_ESTIMACION || this == ACLARACION_REQUERIDA;
    }

}
