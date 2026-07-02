package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieIncompletasUseCase;
import um.tesoreria.core.service.view.ChequeraIncompletaService;
import um.tesoreria.core.model.view.ChequeraIncompleta;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieIncompletasUseCaseImpl implements GetChequeraSerieIncompletasUseCase {

    private final ChequeraIncompletaService chequeraIncompletaService;

    @Override
    public List<ChequeraIncompleta> getAllIncompletas(Integer lectivoId, Integer facultadId, Integer geograficaId) {
        return chequeraIncompletaService.findAllByLectivoIdAndFacultadIdAndGeograficaId(lectivoId, facultadId, geograficaId);
    }
}
