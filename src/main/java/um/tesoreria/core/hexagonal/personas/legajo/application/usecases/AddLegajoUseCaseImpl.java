package um.tesoreria.core.hexagonal.personas.legajo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.AddLegajoUseCase;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.out.LegajoRepository;

@Component
@RequiredArgsConstructor
public class AddLegajoUseCaseImpl implements AddLegajoUseCase {

    private final LegajoRepository legajoRepository;

    @Override
    public Legajo add(Legajo legajo) {
        return legajoRepository.save(legajo);
    }
}