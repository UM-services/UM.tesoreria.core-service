package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto;

import java.math.BigDecimal;

public record AsientoIngresoLineaResponse(
        BigDecimal numeroCuenta,
        String nombreCuenta,
        Byte debita,
        BigDecimal importe) {
}
