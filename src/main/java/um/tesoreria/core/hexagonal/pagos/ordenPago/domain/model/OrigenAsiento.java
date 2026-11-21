package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

/**
 * Comprobante dueño de un asiento propuesto.
 *
 * @param tipo         encabezado (orden de pago directa), factura, entrega (asignación de costos)
 *                     o valor
 * @param referenciaId id del hijo: proveedorMovimientoId de la orden de pago o de la factura,
 *                     entregaId o valorMovimientoId
 */
public record OrigenAsiento(Tipo tipo, Long referenciaId) {

    public enum Tipo {
        ENCABEZADO, FACTURA, ENTREGA, VALOR
    }
}
