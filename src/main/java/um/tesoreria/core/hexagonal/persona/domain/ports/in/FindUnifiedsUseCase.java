package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.persona.domain.model.PersonaKey;
import java.util.List;

public interface FindUnifiedsUseCase {
    List<PersonaKey> findByUnifieds(List<String> unifieds);
}
