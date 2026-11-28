package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Un asiento no cuadrado; {@code diferencia} es {@code totalDebe - totalHaber}. */
public record AsientoNoCuadradoResponse(
        OffsetDateTime fechaContable,
        Integer ordenContable,
        List<LineaDiarioResponse> lineas,
        BigDecimal totalDebe,
        BigDecimal totalHaber,
        BigDecimal diferencia) {
}
