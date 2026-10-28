package um.tesoreria.core.hexagonal.extern.facultad.pendienteInfo.infrastructure.web.mapper;

/** Busca el nombre de una materia que la respuesta de {@code /pendienteinfo} no trae. */
@FunctionalInterface
public interface NombreMateriaResolver {

    /** Devuelve el nombre, o {@code null} si no puede obtenerlo. */
    String resolver(Integer facultadId, Integer planId, String materiaId);
}
