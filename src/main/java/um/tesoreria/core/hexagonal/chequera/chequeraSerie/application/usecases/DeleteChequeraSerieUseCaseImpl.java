package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.DeleteChequeraSerieUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

@Component
@RequiredArgsConstructor
public class DeleteChequeraSerieUseCaseImpl implements DeleteChequeraSerieUseCase {

    private final ChequeraSerieRepository repository;

    @Override
    @Transactional
    public void deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId) {
        repository.deleteAllByFacultadIdAndTipoChequeraIdAndChequeraSerieId(facultadId, tipoChequeraId, chequeraSerieId);
    }
}
