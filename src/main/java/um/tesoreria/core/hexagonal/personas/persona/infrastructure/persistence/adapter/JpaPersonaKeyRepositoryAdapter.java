package um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.adapter;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaKeyRepository;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.mapper.PersonaKeyMapper;
import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.repository.JpaPersonaKeyRepository;

@Component
@RequiredArgsConstructor
public class JpaPersonaKeyRepositoryAdapter implements PersonaKeyRepository {

    private final JpaPersonaKeyRepository repository;
    private final PersonaKeyMapper mapper;

    @Override
    public List<PersonaKey> findAllByUnifiedIn(List<String> keys, List<String> sortProperties) {
        Sort sort = sortProperties == null || sortProperties.isEmpty()
                ? Sort.unsorted()
                : Sort.by(sortProperties.stream().map(Sort.Order::asc).toList());
        return repository.findAllByUnifiedIn(keys, sort).stream().map(mapper::toDomain).toList();
    }

    @Override
    public List<PersonaKey> findAllByStrings(List<String> conditions) {
        return repository.findAllByStrings(conditions).stream().map(mapper::toDomain).toList();
    }
}
