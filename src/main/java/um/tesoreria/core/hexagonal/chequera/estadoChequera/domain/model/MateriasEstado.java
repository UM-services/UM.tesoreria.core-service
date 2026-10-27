package um.tesoreria.core.hexagonal.chequera.estadoChequera.domain.model;

import java.util.List;

/**
 * Materias inscriptas según una fuente (un elemento por fuente consultada). Con
 * {@code estado != OK}, {@code cursos} está vacío y {@code cursoInscripcion}, {@code plan},
 * {@code carrera} y {@code observaciones} son {@code null}.
 */
public record MateriasEstado(
        FuenteMaterias fuente,
        EstadoMaterias estado,
        Integer cursoInscripcion,
        String plan,
        String carrera,
        String observaciones,
        List<CursoMaterias> cursos) {
}
