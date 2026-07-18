package um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in.DeleteChequeraTotalUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.out.ChequeraTotalRepository;

@Component
@RequiredArgsConstructor
public class DeleteChequeraTotalUseCaseImpl implements DeleteChequeraTotalUseCase {

    private final ChequeraTotalRepository repository;

    @Override
    public void deleteAllByChequera(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        repository.deleteAllByChequera(facultadId, tipoChequeraId, chequeraSerieId);
    }
}
