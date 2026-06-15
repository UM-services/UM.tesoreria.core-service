package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.model.view.PersonaKey;
import java.util.List;

public interface FindByStringsUseCase {
    List<PersonaKey> findByStrings(List<String> conditions);
}
