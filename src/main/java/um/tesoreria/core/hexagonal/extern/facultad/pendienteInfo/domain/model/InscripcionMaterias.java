package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.domain.model;

import java.util.List;

/** Inscripción de un alumno con sus materias; {@code observaciones} es {@code null} si está vacía o en blanco. */
public record InscripcionMaterias(
        Integer curso,
        String plan,
        String carrera,
        String observaciones,
        List<MateriaCursada> materias) {
}
