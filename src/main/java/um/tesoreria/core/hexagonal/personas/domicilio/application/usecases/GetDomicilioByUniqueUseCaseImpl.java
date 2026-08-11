package um.tesoreria.core.hexagonal.personas.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.application.exception.DomicilioException;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in.GetDomicilioByUniqueUseCase;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.out.DomicilioRepository;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class GetDomicilioByUniqueUseCaseImpl implements GetDomicilioByUniqueUseCase {
    private final DomicilioRepository repository;

    @Override
    public Domicilio getDomicilioByUnique(BigDecimal personaId, Integer documentoId) {
        return repository.findByUnique(personaId, documentoId)
                .orElseThrow(() -> new DomicilioException(personaId, documentoId));
    }
}
