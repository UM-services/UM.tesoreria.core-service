package um.tesoreria.core.hexagonal.chequera.estadoChequera.infrastructure.web.dto;

import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.EstadoMaterias;
import um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model.FuenteMaterias;

import java.util.List;

public record MateriasEstadoResponse(
        FuenteMaterias fuente,
        EstadoMaterias estado,
        Integer cursoInscripcion,
        String plan,
        String carrera,
        String observaciones,
        List<CursoMateriasResponse> cursos) {
}
