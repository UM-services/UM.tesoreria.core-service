package um.tesoreria.core.hexagonal.chequera.chequeraTotal.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.model.ChequeraTotal;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.in.FindByUniqueChequeraTotalUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraTotal.domain.ports.out.ChequeraTotalRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindByUniqueChequeraTotalUseCaseImpl implements FindByUniqueChequeraTotalUseCase {

    private final ChequeraTotalRepository repository;

    @Override
    public Optional<ChequeraTotal> findByUnique(Integer facultadId, Integer tipoChequeraId, Long chequeraSerieId, Integer productoId) {
        return repository.findByUnique(facultadId, tipoChequeraId, chequeraSerieId, productoId);
    }
}
