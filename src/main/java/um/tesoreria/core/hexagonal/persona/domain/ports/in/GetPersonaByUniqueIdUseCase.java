package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.persona.domain.model.Persona;

public interface GetPersonaByUniqueIdUseCase {
    Persona findByUniqueId(Long uniqueId);
}
