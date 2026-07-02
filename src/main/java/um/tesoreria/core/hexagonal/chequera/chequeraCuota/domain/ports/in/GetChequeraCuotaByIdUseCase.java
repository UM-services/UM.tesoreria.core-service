package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.util.Optional;

public interface GetChequeraCuotaByIdUseCase {
    Optional<ChequeraCuota> getChequeraCuotaById(Long chequeraCuotaId);
}
