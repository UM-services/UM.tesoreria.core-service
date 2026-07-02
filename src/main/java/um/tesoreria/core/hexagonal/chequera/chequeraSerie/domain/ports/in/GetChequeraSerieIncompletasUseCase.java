package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.model.view.ChequeraIncompleta;
import java.util.List;

public interface GetChequeraSerieIncompletasUseCase {
    List<ChequeraIncompleta> getAllIncompletas(Integer lectivoId, Integer facultadId, Integer geograficaId);
}
