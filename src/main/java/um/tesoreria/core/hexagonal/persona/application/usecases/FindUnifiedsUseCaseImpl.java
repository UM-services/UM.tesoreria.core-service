package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.FindUnifiedsUseCase;
import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.service.view.PersonaKeyService;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindUnifiedsUseCaseImpl implements FindUnifiedsUseCase {

    private final PersonaKeyService personaKeyService;

    @Override
    public List<PersonaKey> findByUnifieds(List<String> unifieds) {
        return personaKeyService.findAllByUnifiedIn(unifieds, Sort.by("apellido").ascending());
    }
}
