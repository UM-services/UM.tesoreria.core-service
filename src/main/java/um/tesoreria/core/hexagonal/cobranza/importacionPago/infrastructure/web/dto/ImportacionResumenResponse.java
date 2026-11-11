package um.tesoreria.core.hexagonal.cobranza.importacionPago.infrastructure.web.dto;

import java.time.OffsetDateTime;

public record ImportacionResumenResponse(
        Integer id,
        String nombre,
        OffsetDateTime fecha,
        Integer registros) {
}
