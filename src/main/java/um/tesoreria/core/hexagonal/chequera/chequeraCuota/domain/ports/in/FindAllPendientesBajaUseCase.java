package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.util.List;

public interface FindAllPendientesBajaUseCase {
    List<ChequeraCuota> findAllPendientesBaja(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);
}
