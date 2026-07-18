package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.GetChequeraCuotaByIdUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetChequeraCuotaByIdUseCaseImpl implements GetChequeraCuotaByIdUseCase {

    private final ChequeraCuotaRepository repository;
    private final ChequeraSerieService chequeraSerieService;

    @Override
    public Optional<ChequeraCuota> getChequeraCuotaById(Long chequeraCuotaId) {
        Optional<ChequeraCuota> optionalCuota = repository.findByChequeraCuotaId(chequeraCuotaId);
        if (optionalCuota.isPresent()) {
            ChequeraCuota chequeraCuota = optionalCuota.get();
            if (chequeraCuota.getChequeraId() == null) {
                ChequeraSerie chequeraSerie = chequeraSerieService.findByUnique(chequeraCuota.getFacultadId(),
                        chequeraCuota.getTipoChequeraId(), chequeraCuota.getChequeraSerieId());
                chequeraCuota.setChequeraId(chequeraSerie.getChequeraId());
                chequeraCuota = repository.save(chequeraCuota);
            }
            return Optional.of(chequeraCuota);
        }
        return Optional.empty();
    }
}
