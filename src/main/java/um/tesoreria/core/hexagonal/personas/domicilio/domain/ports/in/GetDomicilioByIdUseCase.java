package um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in;

import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;

import java.util.Optional;

public interface GetDomicilioByIdUseCase {
    Optional<Domicilio> getDomicilioById(Long id);
}
