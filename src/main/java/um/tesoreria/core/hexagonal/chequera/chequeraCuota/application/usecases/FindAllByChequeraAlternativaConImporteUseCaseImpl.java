package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllByChequeraAlternativaConImporteUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import jakarta.transaction.Transactional;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByChequeraAlternativaConImporteUseCaseImpl implements FindAllByChequeraAlternativaConImporteUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    @Transactional
    public List<ChequeraCuota> findAllByChequeraAlternativaConImporte(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        // El chequeraId se completa en memoria para la respuesta; persistirlo es tarea del job
        // de backfill (no de una lectura).
        var cuotas = repository.findAllByChequeraAlternativaConImporte(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
        cuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));
        return cuotas;
    }
}
