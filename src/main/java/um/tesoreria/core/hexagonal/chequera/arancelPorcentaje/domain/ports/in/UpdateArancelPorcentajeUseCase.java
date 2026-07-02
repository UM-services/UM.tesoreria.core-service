package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;

public interface UpdateArancelPorcentajeUseCase {
    ArancelPorcentaje update(ArancelPorcentaje arancelPorcentaje, Long arancelporcentajeId);
}
