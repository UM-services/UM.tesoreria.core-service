package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model;

import java.math.BigDecimal;

/**
 * Suma de los movimientos contables de una cuenta en un rango: lo que se debitó ({@code debita = 1}) y lo que se
 * acreditó ({@code debita = 0}). Una cuenta sin movimientos en el rango no aparece.
 */
public record SumaCuenta(BigDecimal numeroCuenta, BigDecimal debitos, BigDecimal creditos) {
}