package um.tesoreria.core.hexagonal.contable.asientoApertura.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.web.dto.AsientoContableLineaRequest;

import java.util.List;

public record AperturaRequest(String concepto, List<AsientoContableLineaRequest> lineas) {
}
