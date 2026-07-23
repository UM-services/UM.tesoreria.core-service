package um.tesoreria.core.hexagonal.persona.domain.ports.in;

import um.tesoreria.core.hexagonal.persona.domain.model.DeudaExamen;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public interface GetDeudaExamenUseCase {

    DeudaExamen getDeudaExamenByFacultadAndPersona(Integer facultadId, BigDecimal personaId, Integer documentoId, OffsetDateTime fechaExamen);
}
