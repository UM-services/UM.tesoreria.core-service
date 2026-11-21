package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record AsientoContableResponse(
        OffsetDateTime fecha,
        Integer orden,
        String vinculo,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        boolean cuadra,
        List<AsientoContableLineaResponse> lineas) {
}
