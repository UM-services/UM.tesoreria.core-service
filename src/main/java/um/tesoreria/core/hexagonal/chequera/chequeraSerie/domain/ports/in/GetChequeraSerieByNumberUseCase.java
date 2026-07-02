package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import java.util.List;

public interface GetChequeraSerieByNumberUseCase {
    List<ChequeraSerie> getAllByNumber(Integer facultadId, Long chequeraSerieId);
}
