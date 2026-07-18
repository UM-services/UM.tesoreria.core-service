package um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;

import java.util.Optional;

public interface FindByUniqueChequeraTotalUseCase {
    Optional<ChequeraTotal> findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId);
}
