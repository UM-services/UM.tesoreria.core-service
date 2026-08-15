package um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;

import java.util.List;

public interface FindAllLegajosByFacultadIdUseCase {
    List<Legajo> findAllByFacultadId(Integer facultadId);
}