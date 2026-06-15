package um.tesoreria.core.hexagonal.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.in.GetDomicilioByPersonaIdUseCase;
import um.tesoreria.core.hexagonal.domicilio.domain.ports.out.DomicilioRepository;

import java.math.BigDecimal;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetDomicilioByPersonaIdUseCaseImpl implements GetDomicilioByPersonaIdUseCase {
    private final DomicilioRepository repository;

    @Override
    public Optional<Domicilio> getFirstByPersonaId(BigDecimal personaId) {
        return repository.findFirstByPersonaId(personaId);
    }
}
