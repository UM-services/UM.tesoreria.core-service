package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;

import java.math.BigDecimal;
import java.util.Optional;

public interface GetDomicilioByPersonaIdUseCase {
    Optional<Domicilio> getFirstByPersonaId(BigDecimal personaId);
}
