package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import java.math.BigDecimal;

public interface CaptureDomicilioUseCase {
    Integer capture(BigDecimal personaId, Integer documentoId);
}
