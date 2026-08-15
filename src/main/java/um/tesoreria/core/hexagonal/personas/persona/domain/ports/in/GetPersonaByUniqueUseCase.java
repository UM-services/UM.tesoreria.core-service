package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import java.math.BigDecimal;

public interface GetPersonaByUniqueUseCase {
    Persona findByUnique(BigDecimal personaId, Integer documentoId);
}
