package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

public interface GetPersonaByUniqueIdUseCase {
    Persona findByUniqueId(Long uniqueId);
}
