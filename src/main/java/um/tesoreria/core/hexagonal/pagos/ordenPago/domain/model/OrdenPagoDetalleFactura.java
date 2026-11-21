package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;

public record OrdenPagoDetalleFactura(Long proveedorMovimientoId, Integer comprobanteId, Integer prefijo,
                                      Long numeroComprobante, BigDecimal importe, BigDecimal cancelado,
                                      BigDecimal importeAplicado, Byte trazaContable,
                                      java.time.OffsetDateTime fechaContable, Integer ordenContable) {
}
