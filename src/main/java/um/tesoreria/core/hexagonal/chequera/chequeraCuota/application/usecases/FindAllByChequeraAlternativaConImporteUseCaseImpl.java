package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllByChequeraAlternativaConImporteUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.persistence.entity.ChequeraSerieEntity;

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

        List<ChequeraCuota> todasCuotas = repository.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
        todasCuotas.forEach(cuota -> cuota.setChequeraId(chequeraSerie.getChequeraId()));
        repository.saveAll(todasCuotas);

        return repository.findAllByChequeraAlternativaConImporte(facultadId, tipoChequeraId, chequeraSerieId, alternativaId);
    }
}
