package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

public interface MarkSentUseCase {
    ChequeraSerie markSent(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
