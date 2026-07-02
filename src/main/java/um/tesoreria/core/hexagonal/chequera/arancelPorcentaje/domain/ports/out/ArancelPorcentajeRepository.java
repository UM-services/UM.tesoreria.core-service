package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;

import java.util.List;
import java.util.Optional;

public interface ArancelPorcentajeRepository {
    List<ArancelPorcentaje> findAllByAranceltipoId(Integer aranceltipoId);
    Optional<ArancelPorcentaje> findByUnique(Integer aranceltipoId, Integer productoId);
    ArancelPorcentaje add(ArancelPorcentaje arancelPorcentaje);
    Optional<ArancelPorcentaje> findById(Long arancelporcentajeId);
    ArancelPorcentaje update(ArancelPorcentaje arancelPorcentaje);
}
