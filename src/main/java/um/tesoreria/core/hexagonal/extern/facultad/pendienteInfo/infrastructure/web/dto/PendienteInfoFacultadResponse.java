package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Subconjunto de la respuesta de {@code /pendienteinfo} de la facultad que usa el core.
 * Las secciones {@code materias}, {@code materiascarrera} y {@code materiascurso} traen filas de todo el plan;
 * el mapper las cruza con {@code detalles}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PendienteInfoFacultadResponse(
        Inscripcion inscripcion,
        Plan plan,
        Carrera carrera,
        List<Detalle> detalles,
        List<Materia> materias,
        List<MateriaCarrera> materiascarrera,
        List<MateriaCurso> materiascurso) {

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Inscripcion(Integer curso, Integer planId, Integer carreraId, String observaciones) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Plan(String nombre) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Carrera(String nombre) {
    }

    /** Materia inscripta. {@code materia} trae el nombre con el plan del detalle. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Detalle(Integer facultadId, Integer planId, String materiaId, Materia materia) {
    }

    /** Materia de la facultad. También es la forma de la respuesta de {@code /materia/unique}. */
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Materia(Integer facultadId, Integer planId, String materiaId, String nombre) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MateriaCarrera(Integer facultadId, Integer planId, Integer carreraId, String materiaId) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record MateriaCurso(Integer facultadId, Integer planId, Integer carreraId, String materiaId,
                               Integer curso, Integer periodo) {
    }
}
