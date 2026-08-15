package um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;

import java.util.Optional;

public interface UpdateDomicilioUseCase {
    Optional<Domicilio> updateDomicilio(Long id, Domicilio domicilio);
}
