package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllCuotasByChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllCuotasByChequeraUseCaseImpl implements FindAllCuotasByChequeraUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    public List<ChequeraCuota> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
        List<ChequeraCuota> cuotas = repository.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
        for (ChequeraCuota cuota : cuotas) {
            cuota.setChequeraId(chequeraSerie.getChequeraId());
        }
        return cuotas;
    }
}
