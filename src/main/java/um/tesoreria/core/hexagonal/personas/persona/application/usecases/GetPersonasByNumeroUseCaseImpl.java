package um.tesoreria.core.hexagonal.personas.persona.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.persona.domain.model.Persona;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.in.GetPersonasByNumeroUseCase;
import um.tesoreria.core.hexagonal.personas.persona.domain.ports.out.PersonaRepository;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetPersonasByNumeroUseCaseImpl implements GetPersonasByNumeroUseCase {

    private final PersonaRepository repository;

    @Override
    public List<Persona> findAllByNumeroDocumento(BigDecimal numeroDocumento) {
        return repository.findAllByPersonaId(numeroDocumento).stream()
                .sorted(Comparator.comparing(Persona::getDocumentoId))
                .toList();
    }
}
