package um.tesoreria.core.service.facade.sumasysaldos;

import java.math.BigDecimal;
import java.math.RoundingMode;

import um.tesoreria.core.hexagonal.contable.cuenta.domain.model.Cuenta;
import um.tesoreria.core.service.facade.SaldosCuenta;

/** Una cuenta del balance de sumas y saldos. */
public record SumasSaldosFila(
        BigDecimal numeroCuenta,
        String nombre,
        BigDecimal inicialDebitos,
        BigDecimal inicialCreditos,
        BigDecimal debitos,
        BigDecimal creditos,
        BigDecimal saldoDeudor,
        BigDecimal saldoAcreedor) {

    /**
     * Mismo cálculo que el balance anterior: saldo = (inicial debe - inicial haber) + (debe - haber), cada resta
     * redondeada a 2 decimales; si es positivo es deudor y si no, acreedor (cero incluido).
     */
    public static SumasSaldosFila de(Cuenta cuenta, SaldosCuenta saldos) {
        BigDecimal saldo = saldos.inicialDebitos().subtract(saldos.inicialCreditos()).setScale(2, RoundingMode.HALF_UP)
                .add(saldos.debitos().subtract(saldos.creditos()).setScale(2, RoundingMode.HALF_UP));
        BigDecimal deudor = BigDecimal.ZERO;
        BigDecimal acreedor = BigDecimal.ZERO;
        if (saldo.compareTo(BigDecimal.ZERO) > 0) {
            deudor = saldo;
        } else {
            acreedor = saldo.negate().setScale(2, RoundingMode.HALF_UP);
        }
        return new SumasSaldosFila(cuenta.getNumeroCuenta(), cuenta.getNombre(), saldos.inicialDebitos(),
                saldos.inicialCreditos(), saldos.debitos(), saldos.creditos(), deudor, acreedor);
    }
}