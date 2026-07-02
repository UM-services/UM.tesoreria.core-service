package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.util.List;

public interface SaveAllChequeraCuotasUseCase {
    List<ChequeraCuota> saveAll(List<ChequeraCuota> chequeraCuotas);
}
