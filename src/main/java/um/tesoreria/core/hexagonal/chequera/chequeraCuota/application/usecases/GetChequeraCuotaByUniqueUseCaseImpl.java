package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.GetChequeraCuotaByUniqueUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class GetChequeraCuotaByUniqueUseCaseImpl implements GetChequeraCuotaByUniqueUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    public Optional<ChequeraCuota> getChequeraCuotaByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId, Integer alternativaId, Integer cuotaId) {
        log.debug("\n\nProcessing GetChequeraCuotaByUniqueUseCaseImpl.getChequeraCuotaByUnique\n\n");
        Optional<ChequeraCuota> optionalCuota = repository.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId, alternativaId, cuotaId);
        if (optionalCuota.isPresent()) {
            ChequeraCuota chequeraCuota = optionalCuota.get();
            log.debug("ChequeraCuota: {}", chequeraCuota.jsonify());
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
