package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in.*;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ArancelPorcentajeService {

    private final FindAllByArancelTipoIdUseCase findAllByArancelTipoIdUseCase;
    private final FindByUniqueUseCase findByUniqueUseCase;
    private final AddArancelPorcentajeUseCase addArancelPorcentajeUseCase;
    private final UpdateArancelPorcentajeUseCase updateArancelPorcentajeUseCase;

    public List<ArancelPorcentaje> findAllByArancelTipoId(Integer aranceltipoId) {
        return findAllByArancelTipoIdUseCase.findAllByArancelTipoId(aranceltipoId);
    }

    public ArancelPorcentaje findByUnique(Integer aranceltipoId, Integer productoId) {
        return findByUniqueUseCase.findByUnique(aranceltipoId, productoId);
    }

    public ArancelPorcentaje add(ArancelPorcentaje arancelPorcentaje) {
        return addArancelPorcentajeUseCase.add(arancelPorcentaje);
    }

    public ArancelPorcentaje update(ArancelPorcentaje arancelPorcentaje, Long arancelporcentajeId) {
        return updateArancelPorcentajeUseCase.update(arancelPorcentaje, arancelporcentajeId);
    }
}
