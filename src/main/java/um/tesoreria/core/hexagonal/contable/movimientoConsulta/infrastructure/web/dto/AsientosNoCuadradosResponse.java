package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record AsientosNoCuadradosResponse(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        List<AsientoNoCuadradoResponse> asientos) {
}
