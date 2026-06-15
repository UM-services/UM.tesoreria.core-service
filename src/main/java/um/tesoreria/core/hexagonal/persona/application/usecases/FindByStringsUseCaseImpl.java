package um.tesoreria.core.hexagonal.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.persona.domain.ports.in.FindByStringsUseCase;
import um.tesoreria.core.model.view.PersonaKey;
import um.tesoreria.core.service.view.PersonaKeyService;

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
