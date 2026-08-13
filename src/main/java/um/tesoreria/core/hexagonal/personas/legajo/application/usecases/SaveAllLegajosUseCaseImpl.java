package um.tesoreria.core.hexagonal.personas.legajo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.personas.legajo.domain.model.Legajo;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.in.SaveAllLegajosUseCase;
import um.tesoreria.core.hexagonal.personas.legajo.domain.ports.out.LegajoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveAllLegajosUseCaseImpl implements SaveAllLegajosUseCase {

    private final LegajoRepository legajoRepository;

    @Override
    public List<Legajo> saveAll(List<Legajo> legajos) {
        return legajoRepository.saveAll(legajos);
    }
}