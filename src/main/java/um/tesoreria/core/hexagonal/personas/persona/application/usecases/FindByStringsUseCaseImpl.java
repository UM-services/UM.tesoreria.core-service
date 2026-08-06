package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.FindByStringsUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.personas.persona.application.service.PersonaKeyService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindByStringsUseCaseImpl implements FindByStringsUseCase {

    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findByStrings(List<String> conditions) {
        return personaKeyService.findAllByStrings(conditions);
    }
}
