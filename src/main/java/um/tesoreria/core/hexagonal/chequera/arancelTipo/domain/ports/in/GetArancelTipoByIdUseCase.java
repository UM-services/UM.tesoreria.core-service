package um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import java.util.Optional;

public interface GetArancelTipoByIdUseCase {
    Optional<ArancelTipo> getArancelTipoById(Integer id);
}
