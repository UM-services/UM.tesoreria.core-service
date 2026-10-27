package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.PeriodoCursada;

import java.util.List;

public record PeriodoMateriasResponse(PeriodoCursada periodo, List<MateriaInscriptaResponse> materias) {
}
