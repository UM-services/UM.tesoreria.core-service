package um.tesoreria.core.hexagonal.personas.persona.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaKeyRepository;

@Service
@RequiredArgsConstructor
public class PersonaKeyService {

    private final PersonaKeyRepository repository;

    public List<PersonaKey> findAllByUnifiedIn(List<String> keys, List<String> sortProperties) {
        return repository.findAllByUnifiedIn(keys, sortProperties);
    }

    public List<PersonaKey> findAllByStrings(List<String> conditions) {
        return repository.findAllByStrings(conditions);
    }
}
