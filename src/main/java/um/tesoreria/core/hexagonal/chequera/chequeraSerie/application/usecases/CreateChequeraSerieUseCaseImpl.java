package um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.in.CreateChequeraSerieUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.ports.out.ChequeraSerieRepository;

@Component
@RequiredArgsConstructor
public class CreateChequeraSerieUseCaseImpl implements CreateChequeraSerieUseCase {

    private final ChequeraSerieRepository repository;

    @Override
    public ChequeraSerie create(ChequeraSerie chequeraSerie) {
        return repository.save(chequeraSerie);
    }
}
