package um.tesoreria.core.hexagonal.facultad.domain.ports.in;

import java.util.List;

import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;

public interface GetFacultadesConResponsableAcademicaUseCase {
    List<Facultad> getFacultadesConResponsableAcademica();
}
