package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface GetOneActivaImpagaPreviaUseCase {
    Optional<ChequeraCuota> getOneActivaImpagaPrevia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia);
}
