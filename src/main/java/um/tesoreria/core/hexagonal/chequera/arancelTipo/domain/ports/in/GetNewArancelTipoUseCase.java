package um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;

public interface GetNewArancelTipoUseCase {
    ArancelTipo getNewArancelTipo();
}
