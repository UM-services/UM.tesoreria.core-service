package um.tesoreria.core.service.facade.mayor;

import java.math.BigDecimal;

/** Apertura del ejercicio de {@code desde} más los movimientos hasta el día anterior a {@code desde}. */
public record MayorSaldoInicialResponse(BigDecimal debe, BigDecimal haber, BigDecimal saldo) {
}