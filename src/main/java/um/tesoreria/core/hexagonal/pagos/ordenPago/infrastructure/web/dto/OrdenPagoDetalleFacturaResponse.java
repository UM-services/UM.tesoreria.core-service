package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

/** Factura cancelada por la orden de pago. */
public record OrdenPagoDetalleFacturaResponse(Long proveedorMovimientoId,
                                              Integer comprobanteId,
                                              Integer prefijo,
                                              Long numeroComprobante,
                                              BigDecimal importe,
                                              BigDecimal cancelado,
                                              BigDecimal importeAplicado,
                                              Byte trazaContable,
                                              OffsetDateTime fechaContable,
                                              Integer ordenContable) {
}
