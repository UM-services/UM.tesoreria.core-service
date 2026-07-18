package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.CreateChequeraPagoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Component
@RequiredArgsConstructor
public class CreateChequeraPagoUseCaseImpl implements CreateChequeraPagoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public ChequeraPago createChequeraPago(ChequeraPago chequeraPago) {
        return repository.save(chequeraPago);
    }

}
