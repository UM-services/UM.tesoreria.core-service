package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;

/** Importe de una factura de compra que se cancela con la orden de pago. */
public record OrdenPagoFacturaRequest(Long proveedorMovimientoId, BigDecimal pago) {
}
