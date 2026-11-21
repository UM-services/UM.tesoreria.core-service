package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;

/** Gasto de una orden de pago directa: cuenta contable al debe e importe. */
public record ContextoGasto(BigDecimal numeroCuenta, BigDecimal importe) {
}
