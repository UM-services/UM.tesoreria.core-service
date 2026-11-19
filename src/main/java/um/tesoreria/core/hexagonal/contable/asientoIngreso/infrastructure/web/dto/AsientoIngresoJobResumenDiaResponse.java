package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto;

import java.time.OffsetDateTime;

public record AsientoIngresoJobResumenDiaResponse(
        OffsetDateTime fecha,
        int ok,
        int error,
        int sinMovimientos) {
}
