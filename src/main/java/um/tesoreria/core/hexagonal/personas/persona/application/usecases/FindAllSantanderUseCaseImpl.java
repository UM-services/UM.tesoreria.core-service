package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.FindAllSantanderUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllSantanderUseCaseImpl implements FindAllSantanderUseCase {

    private final PersonaRepository repository;

    @Override
    public List<Persona> findAllSantander() {
        return repository.findAllByCbuLike("072%");
    }
}
