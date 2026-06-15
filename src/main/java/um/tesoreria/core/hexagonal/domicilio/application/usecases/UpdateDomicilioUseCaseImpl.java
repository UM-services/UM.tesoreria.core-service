package um.tesoreria.core.hexagonal.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.in.UpdateDomicilioUseCase;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.out.DomicilioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UpdateDomicilioUseCaseImpl implements UpdateDomicilioUseCase {
    private final DomicilioRepository repository;

    @Override
    public Optional<Domicilio> updateDomicilio(Long id, Domicilio domicilio) {
        return repository.update(id, domicilio);
    }
}
