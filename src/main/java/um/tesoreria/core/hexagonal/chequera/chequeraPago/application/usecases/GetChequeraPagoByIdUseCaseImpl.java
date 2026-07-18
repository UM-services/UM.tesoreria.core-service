package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.model.ChequeraPago;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.in.GetChequeraPagoByIdUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraPago.domain.ports.out.ChequeraPagoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetChequeraPagoByIdUseCaseImpl implements GetChequeraPagoByIdUseCase {

    private final ChequeraPagoRepository repository;

    @Override
    public Optional<ChequeraPago> getChequeraPagoById(Long chequeraPagoId) {
        return repository.findByChequeraPagoId(chequeraPagoId);
    }

}
