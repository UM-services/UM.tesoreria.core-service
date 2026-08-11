package um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;

import java.math.BigDecimal;
import java.util.Optional;

public interface GetDomicilioByPersonaIdUseCase {
    Optional<Domicilio> getFirstByPersonaId(BigDecimal personaId);
}
