package um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import java.util.List;

public interface FindAllByFacultadTipoChequeraSerieIdsUseCase {
    List<ChequeraCuota> findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIds(Integer facultadId, Integer tipoChequeraId, List<Long> chequeraSerieIds);
}
