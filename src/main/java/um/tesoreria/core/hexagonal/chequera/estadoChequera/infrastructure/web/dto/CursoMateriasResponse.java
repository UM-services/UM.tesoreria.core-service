package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import java.util.List;

public record CursoMateriasResponse(Integer curso, List<PeriodoMateriasResponse> periodos) {
}
