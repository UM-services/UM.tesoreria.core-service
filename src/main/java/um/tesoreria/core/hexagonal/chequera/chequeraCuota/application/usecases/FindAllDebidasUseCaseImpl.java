package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllDebidasUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import jakarta.transaction.Transactional;
import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllDebidasUseCaseImpl implements FindAllDebidasUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    @Transactional
    public List<ChequeraCuota> findAllDebidas(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        // El chequeraId se completa en memoria para la respuesta; persistirlo es tarea del job
        // de backfill (no de una lectura).
        var cuotas = repository.findAllDebidas(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, referencia);
        cuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));
        return cuotas;
    }
}
