package um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.FindAllByChequeraIdsUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.out.ChequeraCuotaRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByChequeraIdsUseCaseImpl implements FindAllByChequeraIdsUseCase {

    private final ChequeraCuotaRepository repository;

    @Override
    public List<ChequeraCuota> findAllByChequeraIds(List<Long> chequeraIds) {
        return repository.findAllByChequeraIds(chequeraIds);
    }
}
