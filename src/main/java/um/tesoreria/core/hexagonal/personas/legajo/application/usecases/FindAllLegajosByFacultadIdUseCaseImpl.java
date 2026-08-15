package um.tesoreria.core.hexagonal.personas.legajo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.FindAllLegajosByFacultadIdUseCase;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.out.LegajoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllLegajosByFacultadIdUseCaseImpl implements FindAllLegajosByFacultadIdUseCase {

    private final LegajoRepository legajoRepository;

    @Override
    public List<Legajo> findAllByFacultadId(Integer facultadId) {
        return legajoRepository.findAllByFacultadId(facultadId);
    }
}