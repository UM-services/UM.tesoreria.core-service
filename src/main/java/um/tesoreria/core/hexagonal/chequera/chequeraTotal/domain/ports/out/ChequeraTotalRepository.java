package um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.util.List;
import java.util.Optional;

public interface ChequeraTotalRepository {
    List<ChequeraTotal> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
    Optional<ChequeraTotal> findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId);
    Optional<ChequeraTotal> findByChequeraTotalId(Long chequeraTotalId);
    List<ChequeraTotal> saveAll(List<ChequeraTotal> chequeraTotals);
    ChequeraTotal save(ChequeraTotal chequeraTotal);
    void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
