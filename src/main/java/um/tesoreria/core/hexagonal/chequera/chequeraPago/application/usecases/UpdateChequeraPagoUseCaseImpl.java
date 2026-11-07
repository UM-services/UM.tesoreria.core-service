package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.UpdateChequeraPagoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Component
@RequiredArgsConstructor
public class UpdateChequeraPagoUseCaseImpl implements UpdateChequeraPagoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public ChequeraPago updateChequeraPago(ChequeraPago chequeraPago) {
        return repository.save(chequeraPago);
    }

}
