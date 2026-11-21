package um.tesoreria.core.hexagonal.contable.asientoManual.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaRequest;

import java.time.OffsetDateTime;
import java.util.List;

public record AsientoManualRequest(
        OffsetDateTime fecha,
        Integer numero,
        String concepto,
        List<AsientoContableLineaRequest> lineas) {
}
