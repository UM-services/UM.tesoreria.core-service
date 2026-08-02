package um.tesoreria.core.hexagonal.facultad.application.usecases;

import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.in.GetFacultadesConResponsableAcademicaUseCase;
import um.tesoreria.core.hexagonal.facultad.domain.ports.out.FacultadRepository;

@Component
@RequiredArgsConstructor
public class GetFacultadesConResponsableAcademicaUseCaseImpl
        implements GetFacultadesConResponsableAcademicaUseCase {

    private final FacultadRepository repository;

    @Override
    public List<Facultad> getFacultadesConResponsableAcademica() {
        return repository.findAllByGuaraniResponsableAcademicaNotNull();
    }
}
