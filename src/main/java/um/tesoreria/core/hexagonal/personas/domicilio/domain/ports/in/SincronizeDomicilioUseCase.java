package um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;

public interface SincronizeDomicilioUseCase {
    Domicilio sincronize(Domicilio domicilio);
}
