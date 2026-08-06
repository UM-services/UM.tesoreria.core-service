package um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.repository;

import java.util.List;

import um.tesoreria.core.hexagonal.personas.persona.infrastructure.persistence.entity.PersonaKeyEntity;

public interface PersonaKeyRepositoryCustom {

    List<PersonaKeyEntity> findAllByStrings(List<String> conditions);
}
