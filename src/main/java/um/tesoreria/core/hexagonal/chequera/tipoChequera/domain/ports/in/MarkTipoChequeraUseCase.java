package um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.tipoChequera.domain.model.TipoChequera;

public interface MarkTipoChequeraUseCase {
    TipoChequera markTipoChequera(Integer tipoChequeraId, Byte imprimir);
}
