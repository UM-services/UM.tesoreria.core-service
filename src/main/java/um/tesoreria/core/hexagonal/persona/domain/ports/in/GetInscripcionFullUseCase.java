package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.persona.infrastructure.web.dto.InscripcionFullDto;
import java.math.BigDecimal;

public interface GetInscripcionFullUseCase {
    InscripcionFullDto findInscripcionFull(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId);
}
