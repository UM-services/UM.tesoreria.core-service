package um.tesoreria.core.service.facade;

import java.math.BigDecimal;

/**
 * Importes de una cuenta para sumas y saldos: saldo inicial (débitos y créditos) y movimientos del período
 * (débitos y créditos). Sin redondear: tal como salen de los asientos.
 */
public record SaldosCuenta(BigDecimal inicialDebitos, BigDecimal inicialCreditos,
                           BigDecimal debitos, BigDecimal creditos) {

    public static final SaldosCuenta VACIO =
            new SaldosCuenta(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
}