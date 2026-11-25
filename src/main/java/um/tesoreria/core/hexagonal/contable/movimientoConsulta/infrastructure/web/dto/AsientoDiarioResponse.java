package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record AsientoDiarioResponse(
        OffsetDateTime fechaContable,
        Integer ordenContable,
        List<LineaDiarioResponse> lineas,
        BigDecimal totalDebe,
        BigDecimal totalHaber) {
}
