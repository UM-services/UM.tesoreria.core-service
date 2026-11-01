package um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.CursoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriaInscripta;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.MateriasEstado;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.domain.model.PeriodoMaterias;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.dto.CursoMateriasResponse;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.dto.MateriaInscriptaResponse;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.dto.MateriasEstadoResponse;
import um.tesoreria.core.hexagonal.chequera.materiasInscriptas.infrastructure.web.dto.PeriodoMateriasResponse;

import java.util.List;

@Component
public class MateriasInscriptasDtoMapper {

    public List<MateriasEstadoResponse> toResponse(List<MateriasEstado> materias) {
        return materias.stream().map(this::toResponse).toList();
    }

    private MateriasEstadoResponse toResponse(MateriasEstado materias) {
        return new MateriasEstadoResponse(
                materias.fuente(),
                materias.estado(),
                materias.cursoInscripcion(),
                materias.plan(),
                materias.carrera(),
                materias.observaciones(),
                materias.cursos().stream().map(this::toResponse).toList());
    }

    private CursoMateriasResponse toResponse(CursoMaterias curso) {
        return new CursoMateriasResponse(
                curso.curso(),
                curso.periodos().stream().map(this::toResponse).toList());
    }

    private PeriodoMateriasResponse toResponse(PeriodoMaterias periodo) {
        return new PeriodoMateriasResponse(
                periodo.periodo(),
                periodo.materias().stream().map(this::toResponse).toList());
    }

    private MateriaInscriptaResponse toResponse(MateriaInscripta materia) {
        return new MateriaInscriptaResponse(materia.materiaId(), materia.nombre());
    }
}
