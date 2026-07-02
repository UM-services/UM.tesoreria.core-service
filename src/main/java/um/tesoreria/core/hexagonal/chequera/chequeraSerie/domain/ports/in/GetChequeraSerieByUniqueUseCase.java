package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import java.util.Optional;

public interface GetChequeraSerieByUniqueUseCase {
    Optional<ChequeraSerie> getByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
    Optional<ChequeraSerie> getByUniqueExtended(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
}
