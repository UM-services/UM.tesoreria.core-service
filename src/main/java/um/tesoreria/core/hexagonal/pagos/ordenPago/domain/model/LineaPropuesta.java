package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model;

import java.math.BigDecimal;

/**
 * Línea de un asiento propuesto. {@code debita = 1} es debe y {@code debita = 0} es haber,
 * igual que en {@code movcon}.
 */
public record LineaPropuesta(BigDecimal numeroCuenta, Byte debita, BigDecimal importe,
                             Integer comprobanteId, Integer proveedorId, String concepto) {
}
