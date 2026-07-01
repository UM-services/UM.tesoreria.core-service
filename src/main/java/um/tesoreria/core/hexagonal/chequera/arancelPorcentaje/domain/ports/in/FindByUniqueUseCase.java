package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;

public interface FindByUniqueUseCase {
    ArancelPorcentaje findByUnique(Integer aranceltipoId, Integer productoId);
}
