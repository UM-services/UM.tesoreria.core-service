package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.exception.PersonaException;
import um.tesoreria.core.hexagonal.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.GetPersonaByUniqueUseCase;
import um.tesoreria.core.hexagonal.persona.domain.ports.out.PersonaRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GetPersonaByUniqueUseCaseImpl implements GetPersonaByUniqueUseCase {

    private final PersonaRepository repository;

    @Override
    public Persona findByUnique(BigDecimal personaId, Integer documentoId) {
        return repository.findByPersonaIdAndDocumentoId(personaId, documentoId)
                .orElseThrow(() -> new PersonaException(personaId, documentoId));
    }
}
