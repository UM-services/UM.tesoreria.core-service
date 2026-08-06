package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.FindUnifiedsUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaKeyService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindUnifiedsUseCaseImpl implements FindUnifiedsUseCase {

    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findByUnifieds(List<String> unifieds) {
        return personaKeyService.findAllByUnifiedIn(unifieds, List.of("apellido"));
    }
}
