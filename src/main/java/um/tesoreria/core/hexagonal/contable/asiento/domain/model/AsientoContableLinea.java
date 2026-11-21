package um.tesoreria.core.hexagonal.contable.asiento.domain.model;

import java.math.BigDecimal;

/** Línea de un asiento contable manual / apertura / cierre. */
public record AsientoContableLinea(
        BigDecimal numeroCuenta,
        String nombreCuenta,
        Byte debita,
        BigDecimal importe) {
}
