package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto;

import java.time.OffsetDateTime;

public record AsientoIngresoJobErrorItemResponse(
        OffsetDateTime fecha,
        Integer tipoPagoId,
        String tipoPagoNombre,
        String codigo,
        String mensaje) {
}
