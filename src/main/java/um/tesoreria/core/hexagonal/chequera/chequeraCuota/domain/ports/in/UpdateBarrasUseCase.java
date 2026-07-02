package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.util.List;

public interface UpdateBarrasUseCase {
    List<ChequeraCuota> updateBarras(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);
    String calculateCodigoBarras(ChequeraCuota chequeraCuota);
}
