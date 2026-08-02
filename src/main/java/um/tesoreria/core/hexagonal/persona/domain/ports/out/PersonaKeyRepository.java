package um.tesoreria.core.hexagonal.persona.domain.ports.out;

import java.util.List;

import um.tesoreria.core.hexagonal.persona.domain.model.PersonaKey;

public interface PersonaKeyRepository {

    List<PersonaKey> findAllByUnifiedIn(List<String> keys, List<String> sortProperties);

    List<PersonaKey> findAllByStrings(List<String> conditions);
}
