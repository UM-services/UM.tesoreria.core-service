package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.GetPersonaByUniqueIdUseCase;
import um.tesoreria.core.hexagonal.persona.domain.ports.out.PersonaRepository;

@Component
@RequiredArgsConstructor
public class GetPersonaByUniqueIdUseCaseImpl implements GetPersonaByUniqueIdUseCase {

    private final PersonaRepository repository;

    @Override
    public Persona findByUniqueId(Long uniqueId) {
        return repository.findByUniqueId(uniqueId)
                .orElseThrow(() -> new PersonaException(uniqueId));
    }
}
