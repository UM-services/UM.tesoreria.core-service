package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in.FindAllByArancelTipoIdUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.out.ArancelPorcentajeRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByArancelTipoIdUseCaseImpl implements FindAllByArancelTipoIdUseCase {
    private final ArancelPorcentajeRepository repository;

    @Override
    public List<ArancelPorcentaje> findAllByArancelTipoId(Integer aranceltipoId) {
        return repository.findAllByAranceltipoId(aranceltipoId);
    }
}
