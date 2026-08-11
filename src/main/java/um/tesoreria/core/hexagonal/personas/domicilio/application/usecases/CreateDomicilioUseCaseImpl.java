package um.tesoreria.core.hexagonal.personas.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in.CreateDomicilioUseCase;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.out.DomicilioRepository;

@Component
@RequiredArgsConstructor
public class CreateDomicilioUseCaseImpl implements CreateDomicilioUseCase {
    private final DomicilioRepository repository;

    @Override
    public Domicilio createDomicilio(Domicilio domicilio) {
        return repository.create(domicilio);
    }
}
