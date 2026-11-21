package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto;

import java.math.BigDecimal;

public record AsientoContableLineaResponse(
        BigDecimal numeroCuenta,
        String nombreCuenta,
        Byte debita,
        BigDecimal importe) {
}
