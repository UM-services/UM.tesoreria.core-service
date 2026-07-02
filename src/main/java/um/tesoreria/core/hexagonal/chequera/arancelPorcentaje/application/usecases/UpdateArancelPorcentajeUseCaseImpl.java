package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.exception.ArancelPorcentajeException;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in.UpdateArancelPorcentajeUseCase;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.out.ArancelPorcentajeRepository;

@Component
@RequiredArgsConstructor
public class UpdateArancelPorcentajeUseCaseImpl implements UpdateArancelPorcentajeUseCase {
    private final ArancelPorcentajeRepository repository;

    @Override
    public ArancelPorcentaje update(ArancelPorcentaje arancelPorcentaje, Long arancelporcentajeId) {
        return repository.findById(arancelporcentajeId)
                .map(existing -> {
                    existing.setAranceltipoId(arancelPorcentaje.getAranceltipoId());
                    existing.setProductoId(arancelPorcentaje.getProductoId());
                    existing.setPorcentaje(arancelPorcentaje.getPorcentaje());
                    return repository.update(existing);
                })
                .orElseThrow(() -> new ArancelPorcentajeException(arancelporcentajeId));
    }
}
