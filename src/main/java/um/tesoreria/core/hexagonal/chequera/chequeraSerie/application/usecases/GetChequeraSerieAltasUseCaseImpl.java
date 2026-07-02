package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieAltasUseCase;
import um.tesoreria.core.service.view.ChequeraSerieAltaFullService;
import um.tesoreria.core.service.view.ChequeraSerieAltaService;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAlta;
import um.tesoreria.core.kotlin.model.view.ChequeraSerieAltaFull;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieAltasUseCaseImpl implements GetChequeraSerieAltasUseCase {

    private final ChequeraSerieAltaService chequeraSerieAltaService;
    private final ChequeraSerieAltaFullService chequeraSerieAltaFullService;

    @Override
    public List<ChequeraSerieAlta> getAllAltas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return chequeraSerieAltaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
    }

    @Override
    public List<ChequeraSerieAltaFull> getAllAltasFull(Integer lectivoId, Integer facultadId, Integer geograficaId, Integer tipoChequeraId, OffsetDateTime fechaDesdePrimerVencimiento, ChequeraCuotaService chequeraCuotaService) {
        return chequeraSerieAltaFullService.findAllByLectivoIdAndFacultadIdAndGeograficaIdAndTipoChequeraId(lectivoId, facultadId, geograficaId, tipoChequeraId, fechaDesdePrimerVencimiento, chequeraCuotaService);
    }
}
