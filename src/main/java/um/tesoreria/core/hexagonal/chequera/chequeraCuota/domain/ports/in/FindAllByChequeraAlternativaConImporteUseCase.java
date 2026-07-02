package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.util.List;

public interface FindAllByChequeraAlternativaConImporteUseCase {
    List<ChequeraCuota> findAllByChequeraAlternativaConImporte(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);
}
