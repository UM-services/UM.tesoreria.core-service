package um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in.CreateChequeraTotalUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.out.ChequeraTotalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateChequeraTotalUseCaseImpl implements CreateChequeraTotalUseCase {

    private final ChequeraTotalRepository repository;

    @Override
    public List<ChequeraTotal> createAll(List<ChequeraTotal> chequeraTotals) {
        return repository.saveAll(chequeraTotals);
    }
}
