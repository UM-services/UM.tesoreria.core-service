package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model;

/**
 * Materia inscripta de un alumno según la facultad. {@code curso} y {@code periodo} son
 * {@code null} si la materia no figura en {@code matxcarrera} y {@code matxcurso}; {@code periodo}
 * es el código crudo de la facultad (0 anual, 1 primer semestre, 2 segundo semestre).
 */
public record MateriaCursada(
        Integer facultadId,
        Integer planId,
        String materiaId,
        String nombre,
        Integer curso,
        Integer periodo) {
}
