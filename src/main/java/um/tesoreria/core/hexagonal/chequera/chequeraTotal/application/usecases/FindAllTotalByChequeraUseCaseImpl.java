package um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in.FindAllTotalByChequeraUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.out.ChequeraTotalRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllTotalByChequeraUseCaseImpl implements FindAllTotalByChequeraUseCase {

    private final ChequeraTotalRepository repository;

    @Override
    public List<ChequeraTotal> findAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        return repository.findAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }
}
