package um.tesoreria.core.hexagonal.personas.domicilio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.model.Domicilio;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.in.SincronizeDomicilioUseCase;
import um.tesoreria.core.hexagonal.personas.domicilio.domain.ports.out.DomicilioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class SincronizeDomicilioUseCaseImpl implements SincronizeDomicilioUseCase {

    private final DomicilioRepository repository;

    @Override
    public Domicilio sincronize(Domicilio domicilio) {
        return repository.findByUnique(domicilio.getPersonaId(), domicilio.getDocumentoId())
                .map(existing -> repository.update(existing.getDomicilioId(), domicilio))
                .orElseGet(() -> Optional.ofNullable(repository.create(domicilio)))
                .orElse(null);
    }
}
