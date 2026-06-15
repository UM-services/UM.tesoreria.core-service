package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;

import java.util.Optional;

public interface UpdateDomicilioUseCase {
    Optional<Domicilio> updateDomicilio(Long id, Domicilio domicilio);
}
