package um.tesoreria.core.hexagonal.chequeraCuota.domain.ports.out;

import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequeraCuota.domain.model.DeudaData;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ChequeraCuotaRepository {

    DeudaData findDeudaData(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    Optional<ChequeraCuota> findCuota1(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId);

    List<ChequeraCuota> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId);

}
