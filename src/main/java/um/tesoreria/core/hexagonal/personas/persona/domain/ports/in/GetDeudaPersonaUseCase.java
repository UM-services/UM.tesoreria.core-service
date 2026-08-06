package um.tesoreria.core.hexagonal.personas.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.persona.infrastructure.web.dto.DeudaPersonaDto;
import java.math.BigDecimal;

public interface GetDeudaPersonaUseCase {
    DeudaPersonaDto deudaByPersona(BigDecimal personaId, Integer documentoId);
    DeudaPersonaDto deudaByPersonaExtended(BigDecimal personaId, Integer documentoId);
}
