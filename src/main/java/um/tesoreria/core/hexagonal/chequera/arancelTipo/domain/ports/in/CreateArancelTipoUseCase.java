package um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.arancelTipo.domain.model.ArancelTipo;

public interface CreateArancelTipoUseCase {
    ArancelTipo createArancelTipo(ArancelTipo arancelTipo);
}
