package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.GetPersonaByIdUseCase;
import um.tesoreria.core.hexagonal.persona.domain.ports.out.PersonaRepository;

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
