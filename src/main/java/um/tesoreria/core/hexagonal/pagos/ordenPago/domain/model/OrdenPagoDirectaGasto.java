package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;

/** Gasto informado por el usuario en una orden de pago directa. */
public record OrdenPagoDirectaGasto(BigDecimal numeroCuenta, BigDecimal importe) {
}
