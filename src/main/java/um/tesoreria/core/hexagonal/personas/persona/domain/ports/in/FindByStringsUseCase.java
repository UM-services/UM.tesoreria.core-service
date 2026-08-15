package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.PersonaKey;
import java.util.List;

public interface FindByStringsUseCase {
    List<PersonaKey> findByStrings(List<String> conditions);
}
