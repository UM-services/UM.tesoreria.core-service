package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;

public interface UpdateChequeraCuotaUseCase {
    ChequeraCuota update(ChequeraCuota newChequeraCuota, Long chequeraCuotaId);
}
