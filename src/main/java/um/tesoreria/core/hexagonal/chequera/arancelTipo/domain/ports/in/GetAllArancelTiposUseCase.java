package um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;
import java.util.List;

public interface GetAllArancelTiposUseCase {
    List<ArancelTipo> getAllArancelTipos();
}
