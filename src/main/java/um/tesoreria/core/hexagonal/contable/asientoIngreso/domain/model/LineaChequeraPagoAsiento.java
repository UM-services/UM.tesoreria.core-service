package um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model;

import java.math.BigDecimal;

/**
 * Una línea a escribir en {@code chequera_pago_asiento}. {@code item = 1} es el debe (cuenta de
 * pago de la facultad) y {@code item = 2} el haber (imputación del producto).
 */
public record LineaChequeraPagoAsiento(PagoIngreso pago, int item, BigDecimal cuenta, byte debita,
                                       BigDecimal importe) {
}
