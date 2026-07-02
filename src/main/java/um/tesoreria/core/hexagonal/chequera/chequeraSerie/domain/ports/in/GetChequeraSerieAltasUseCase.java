package um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;

import java.time.OffsetDateTime;
import java.util.List;

public interface GetChequeraSerieAltasUseCase {
    List<ChequeraSerieAlta> getAllAltas(Integer lectivoId, Integer facultadId, Integer geograficaId);
    List<ChequeraSerieAltaFull> getAllAltasFull(Integer lectivoId, Integer facultadId, Integer geograficaId, Integer tipoChequeraId, OffsetDateTime fechaDesdePrimerVencimiento, ChequeraCuotaService chequeraCuotaService);
}
