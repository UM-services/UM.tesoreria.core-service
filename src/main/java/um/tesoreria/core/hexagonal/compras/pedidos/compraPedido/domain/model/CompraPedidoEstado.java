package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

/**
 * Estados del pedido de compra.
 *
 * <p>Circuito de envío: el solicitante presenta el pedido ({@code BORRADOR -> PENDIENTE_ENVIO});
 * el autorizante de envío aprueba ({@code -> EN_REVISION_COMPRAS}, el pedido queda a cargo del
 * dpto. de compras) o rechaza ({@code -> RECHAZADO}, vuelve al solicitante). El solicitante puede
 * corregir y volver a presentar un rechazado, o descartarlo ({@code -> DESCARTADO}).</p>
 *
 * <p>Circuito de presupuesto (autoridad por monto): el dpto. de compras revisa y carga el valor
 * estimado ({@code EN_REVISION_COMPRAS -> PENDIENTE_AUTORIZACION_PRESUPUESTO}); luego un usuario
 * con perfil de autoridad por monto autoriza ({@code -> AUTORIZADO_PRESUPUESTO}) o rechaza
 * ({@code -> RECHAZADO}) el inicio del proceso de pedido de presupuesto.</p>
 *
 * <p>La edición del pedido está permitida mientras no haya pasado a compras: en
 * {@link #BORRADOR}, {@link #RECHAZADO} y los estados previos de la etapa de estimación.</p>
 */
public enum CompraPedidoEstado {

    BORRADOR,
    PENDIENTE_ESTIMACION,
    PENDIENTE_ENVIO,
    ACLARACION_REQUERIDA,
    /**
     * @deprecated reemplazado por {@link #EN_REVISION_COMPRAS} al incorporarse la revisión de
     *             compras. Se conserva sólo para poder leer pedidos históricos sin migrar.
     */
    @Deprecated
    ENVIADO,
    EN_REVISION_COMPRAS,
    PENDIENTE_AUTORIZACION_PRESUPUESTO,
    AUTORIZADO_PRESUPUESTO,
    RECHAZADO,
    DESCARTADO;

    public boolean esEditable() {
        return this == BORRADOR || this == RECHAZADO
                || this == PENDIENTE_ESTIMACION || this == ACLARACION_REQUERIDA;
    }

}
