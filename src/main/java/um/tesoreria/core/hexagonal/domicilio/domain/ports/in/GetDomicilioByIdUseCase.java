package um.tesoreria.core.hexagonal.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;

import java.util.Optional;

public interface GetDomicilioByIdUseCase {
    Optional<Domicilio> getDomicilioById(Long id);
}
