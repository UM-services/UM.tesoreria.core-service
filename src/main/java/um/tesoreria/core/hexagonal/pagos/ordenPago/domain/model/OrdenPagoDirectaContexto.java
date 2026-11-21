package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/**
 * Datos de una orden de pago directa (sin proveedor): gastos al debe contra la cuenta de
 * obligaciones al haber, más los valores con los que se cancela.
 */
public record OrdenPagoDirectaContexto(Integer comprobanteId, Byte debita, Integer prefijo,
                                       Long numeroComprobante, OffsetDateTime fechaComprobante,
                                       OffsetDateTime fechaContableOrdenPago, Integer ordenContableOrdenPago,
                                       Long proveedorMovimientoId, BigDecimal numeroCuentaObligacion,
                                       BigDecimal importe, List<ContextoGasto> gastos,
                                       List<ContextoValor> valores) {

    /** Copia con el asiento de encabezado ya reservado (fecha, orden y orden de pago creada). */
    public OrdenPagoDirectaContexto conEncabezado(OffsetDateTime fechaContable, Integer ordenContable,
                                                  Long proveedorMovimientoId) {
        return new OrdenPagoDirectaContexto(comprobanteId, debita, prefijo, numeroComprobante,
                fechaComprobante, fechaContable, ordenContable, proveedorMovimientoId,
                numeroCuentaObligacion, importe, gastos, valores);
    }

    public String conceptoPago() {
        return String.format("Pago %04d-%08d", prefijo == null ? 0 : prefijo,
                numeroComprobante == null ? 0L : numeroComprobante);
    }
}
