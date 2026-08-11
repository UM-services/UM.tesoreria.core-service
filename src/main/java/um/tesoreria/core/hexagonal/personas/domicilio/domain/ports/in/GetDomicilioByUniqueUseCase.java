package um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;

import java.math.BigDecimal;

public interface GetDomicilioByUniqueUseCase {
    Domicilio getDomicilioByUnique(BigDecimal personaId, Integer documentoId);
}
