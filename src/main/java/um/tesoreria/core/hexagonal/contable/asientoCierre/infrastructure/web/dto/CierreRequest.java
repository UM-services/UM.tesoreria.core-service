package um.tesoreria.core.hexagonal.contable.asientoCierre.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaRequest;

import java.util.List;

public record CierreRequest(String concepto, List<AsientoContableLineaRequest> lineas) {
}
