package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import java.util.Optional;

public interface GetChequeraSerieByIdUseCase {
    Optional<ChequeraSerie> getById(Long chequeraId);
    Optional<ChequeraSerie> getByIdExtended(Long chequeraId);
}
