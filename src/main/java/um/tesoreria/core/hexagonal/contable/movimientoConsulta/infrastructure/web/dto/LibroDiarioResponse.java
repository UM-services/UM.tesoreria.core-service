package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record LibroDiarioResponse(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        List<AsientoDiarioResponse> asientos,
        BigDecimal totalDebe,
        BigDecimal totalHaber) {
}
