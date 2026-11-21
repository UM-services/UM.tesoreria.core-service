package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

import java.math.BigDecimal;

/** Gasto de una orden de pago directa: cuenta contable al debe e importe. */
public record OrdenPagoDirectaGastoRequest(BigDecimal numeroCuenta, BigDecimal importe) {
}
