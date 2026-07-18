package um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

public interface UpdateChequeraTotalUseCase {
    ChequeraTotal update(ChequeraTotal chequeraTotal, Long chequeraTotalId);
}
