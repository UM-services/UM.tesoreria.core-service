package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllByChequeraAlternativaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import jakarta.transaction.Transactional;
import um.tesoreria.core.util.Jsonifier;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FindAllByChequeraAlternativaUseCaseImpl implements FindAllByChequeraAlternativaUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    @Transactional
    public List<ChequeraCuota> findAllByChequeraAlternativa(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId) {
        log.debug("\n\nProcessing FindAllByChequeraAlternativaUseCaseImpl.findAllByChequeraAlternativa\n\n");
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);

        // El chequeraId se completa en memoria para la respuesta; persistirlo es tarea del job
        // de backfill (no de una lectura).
        var cuotas = repository.findAllByChequeraAlternativa(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
        cuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));
        log.debug("\n\nFindAllByChequeraAlternativaUseCaseImpl.findAllByChequeraAlternativa.cuotas -> {}\n\n", Jsonifier.builder(cuotas).build());
        return cuotas;
    }
}
