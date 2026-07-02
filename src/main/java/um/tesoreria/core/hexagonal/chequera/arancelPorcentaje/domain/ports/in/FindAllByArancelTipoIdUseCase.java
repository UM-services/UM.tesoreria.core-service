package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import java.util.List;

public interface FindAllByArancelTipoIdUseCase {
    List<ArancelPorcentaje> findAllByArancelTipoId(Integer aranceltipoId);
}
