package um.tesoreria.core.hexagonal.facultad.application.usecases;

import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import um.tesoreria.core.hexagonal.facultad.domain.model.Facultad;
import um.tesoreria.core.hexagonal.facultad.domain.ports.in.GetFacultadByGuaraniResponsableAcademicaUseCase;
import um.tesoreria.core.hexagonal.facultad.domain.ports.out.FacultadRepository;

@Component
@RequiredArgsConstructor
public class GetFacultadByGuaraniResponsableAcademicaUseCaseImpl
        implements GetFacultadByGuaraniResponsableAcademicaUseCase {
    private final FacultadRepository repository;

    @Override
    public Optional<Facultad> getByGuaraniResponsableAcademica(Integer responsableAcademica) {
        return repository.findByGuaraniResponsableAcademica(responsableAcademica);
    }
}
