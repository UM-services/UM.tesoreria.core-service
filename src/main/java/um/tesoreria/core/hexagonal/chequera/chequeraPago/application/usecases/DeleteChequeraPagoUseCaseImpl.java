package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.DeleteChequeraPagoUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

@Component
@RequiredArgsConstructor
public class DeleteChequeraPagoUseCaseImpl implements DeleteChequeraPagoUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public void deleteChequeraPago(Long chequeraPagoId) {
        repository.deleteById(chequeraPagoId);
    }

}
