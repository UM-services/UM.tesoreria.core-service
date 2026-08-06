package um.tesoreria.core.hexagonal.facultad.domain.ports.in;

import java.util.Optional;

import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;

public interface GetFacultadByGuaraniResponsableAcademicaUseCase {
    Optional<Facultad> getByGuaraniResponsableAcademica(Integer responsableAcademica);
}
