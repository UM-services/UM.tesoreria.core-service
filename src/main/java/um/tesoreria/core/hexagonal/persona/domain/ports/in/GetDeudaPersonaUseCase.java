package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.model.dto.DeudaPersonaDto;
import java.math.BigDecimal;

public interface GetDeudaPersonaUseCase {
    DeudaPersonaDto deudaByPersona(BigDecimal personaId, Integer documentoId);
    DeudaPersonaDto deudaByPersonaExtended(BigDecimal personaId, Integer documentoId);
}
