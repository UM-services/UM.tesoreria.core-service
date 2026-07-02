package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.GetChequeraSerieByIdUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetChequeraSerieByIdUseCaseImpl implements GetChequeraSerieByIdUseCase {

    private final ChequeraSerieRepository repository;
    private final CalculateDeudaUseCase calculateDeudaUseCase;

    @Override
    public Optional<ChequeraSerie> getById(Long chequeraId) {
        return repository.findByChequeraId(chequeraId);
    }

    @Override
    public Optional<ChequeraSerie> getByIdExtended(Long chequeraId) {
        return repository.findByChequeraId(chequeraId)
                .map(chequera -> {
                    var deuda = calculateDeudaUseCase.calculateDeuda(chequera);
                    chequera.setCuotasDeuda(deuda.getCuotas());
                    chequera.setImporteDeuda(deuda.getDeuda());
                    return chequera;
                });
    }
}
