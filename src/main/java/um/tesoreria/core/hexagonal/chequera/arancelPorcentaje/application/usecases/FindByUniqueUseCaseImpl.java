package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.exception.ArancelPorcentajeException;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in.FindByUniqueUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.out.ArancelPorcentajeRepository;

@Component
@RequiredArgsConstructor
public class FindByUniqueUseCaseImpl implements FindByUniqueUseCase {
    private final ArancelPorcentajeRepository repository;

    @Override
    public ArancelPorcentaje findByUnique(Integer aranceltipoId, Integer productoId) {
        return repository.findByUnique(aranceltipoId, productoId)
                .orElseThrow(() -> new ArancelPorcentajeException(aranceltipoId, productoId));
    }
}
