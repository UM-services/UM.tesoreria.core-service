package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

public interface UpdateChequeraSerieUseCase {
    ChequeraSerie update(ChequeraSerie newChequeraSerie, Long chequeraId);
}
