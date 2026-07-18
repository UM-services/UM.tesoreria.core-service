package um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.util.List;

public interface FindAllTotalByChequeraUseCase {
    List<ChequeraTotal> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
