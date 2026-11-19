package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.chequera.emision.domain.model.FuenteCompletado;

public record CompletarBorradorRequest(FuenteCompletado fuente) {
}
