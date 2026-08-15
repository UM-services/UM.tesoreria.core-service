package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;

public interface SavePersonaUseCase {
    Persona create(Persona persona);
    Persona update(Persona persona, Long uniqueId);
}
