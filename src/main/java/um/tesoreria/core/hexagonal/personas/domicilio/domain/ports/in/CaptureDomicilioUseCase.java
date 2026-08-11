package um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in;

import java.math.BigDecimal;

public interface CaptureDomicilioUseCase {
    Integer capture(BigDecimal personaId, Integer documentoId);
}
