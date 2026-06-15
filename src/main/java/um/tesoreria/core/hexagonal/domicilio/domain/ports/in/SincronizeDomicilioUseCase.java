package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;

public interface SincronizeDomicilioUseCase {
    Domicilio sincronize(Domicilio domicilio);
}
