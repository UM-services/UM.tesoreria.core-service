package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto;

import java.math.BigDecimal;

public record AsientoContableLineaRequest(
        BigDecimal numeroCuenta,
        Byte debita,
        BigDecimal importe) {
}
