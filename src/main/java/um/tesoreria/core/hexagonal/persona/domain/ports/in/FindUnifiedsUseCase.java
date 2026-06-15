package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.model.view.PersonaKey;
import java.util.List;

public interface FindUnifiedsUseCase {
    List<PersonaKey> findByUnifieds(List<String> unifieds);
}
