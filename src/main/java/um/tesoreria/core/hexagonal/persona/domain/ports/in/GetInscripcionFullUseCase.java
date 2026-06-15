package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.extern.model.dto.InscripcionFullDto;
import java.math.BigDecimal;

public interface GetInscripcionFullUseCase {
    InscripcionFullDto findInscripcionFull(Integer facultadId, BigDecimal personaId, Integer documentoId, Integer lectivoId);
}
