package um.tesoreria.core.hexagonal.personas.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in.GetDomicilioByIdUseCase;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.out.DomicilioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetDomicilioByIdUseCaseImpl implements GetDomicilioByIdUseCase {
    private final DomicilioRepository repository;

    @Override
    public Optional<Domicilio> getDomicilioById(Long id) {
        return repository.findById(id);
    }
}
