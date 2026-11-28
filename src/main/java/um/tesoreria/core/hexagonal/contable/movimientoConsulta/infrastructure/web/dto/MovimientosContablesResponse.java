package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record MovimientosContablesResponse(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        List<LineaMovimientoContableResponse> lineas,
        BigDecimal totalDebe,
        BigDecimal totalHaber) {
}
