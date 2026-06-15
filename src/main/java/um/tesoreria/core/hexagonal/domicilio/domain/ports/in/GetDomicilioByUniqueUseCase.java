package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;

import java.math.BigDecimal;
import java.util.Optional;

public interface GetDomicilioByUniqueUseCase {
    Domicilio getDomicilioByUnique(BigDecimal personaId, Integer documentoId);
}
