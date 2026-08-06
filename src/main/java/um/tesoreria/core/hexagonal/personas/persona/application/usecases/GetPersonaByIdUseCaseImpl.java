package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.application.exception.PersonaException;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.GetPersonaByIdUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GetPersonaByIdUseCaseImpl implements GetPersonaByIdUseCase {

    private final PersonaRepository repository;

    @Override
    public Persona findByPersonaId(BigDecimal personaId) {
        return repository.findTopByPersonaId(personaId)
                .orElseThrow(() -> new PersonaException(personaId));
    }
}
