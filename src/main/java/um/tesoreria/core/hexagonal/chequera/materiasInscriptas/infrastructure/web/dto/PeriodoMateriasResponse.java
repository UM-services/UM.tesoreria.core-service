package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoCursada;

import java.util.List;

public record PeriodoMateriasResponse(PeriodoCursada periodo, List<MateriaInscriptaResponse> materias) {
}
