package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;

public interface UpdateTipoChequeraUseCase {
    TipoChequera updateTipoChequera(TipoChequera tipoChequera, Integer tipoChequeraId);
}
