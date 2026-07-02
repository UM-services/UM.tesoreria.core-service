package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.GetOneActivaImpagaPreviaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetOneActivaImpagaPreviaUseCaseImpl implements GetOneActivaImpagaPreviaUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    public Optional<ChequeraCuota> getOneActivaImpagaPrevia(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer alternativaId, OffsetDateTime referencia) {
        Optional<ChequeraCuota> optionalCuota = repository.findOneActivaImpagaPrevia(facultadId, tipoChequeraId, chequeraSerieId, alternativaId, referencia);
        if (optionalCuota.isPresent()) {
            ChequeraCuota chequeraCuota = optionalCuota.get();
            if (chequeraCuota.getChequeraId() == null) {
                ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(facultadId, tipoChequeraId, chequeraSerieId);
                chequeraCuota.setChequeraId(chequeraSerie.getChequeraId());
                chequeraCuota = repository.save(chequeraCuota);
            }
            return Optional.of(chequeraCuota);
        }
        return Optional.empty();
    }
}
