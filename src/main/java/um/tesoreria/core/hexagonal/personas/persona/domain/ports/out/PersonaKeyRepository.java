package um.tesoreria.core.hexagonal.personas.persona.domain.ports.out;

import java.util.List;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;

public interface PersonaKeyRepository {

    List<PersonaKey> findAllByUnifiedIn(List<String> keys, List<String> sortProperties);

    List<PersonaKey> findAllByStrings(List<String> conditions);
}
