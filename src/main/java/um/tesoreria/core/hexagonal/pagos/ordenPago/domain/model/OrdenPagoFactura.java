package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;

/** Factura que se cancela con la orden de pago, con el importe que se le aplica. */
public record OrdenPagoFactura(Long proveedorMovimientoId, BigDecimal pago) {
}
