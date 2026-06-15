package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindAllSinImputarUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllSinImputarUseCaseImpl implements FindAllSinImputarUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public List<MercadoPagoContext> findAllSinImputar() {
        return repository.findAllByStatusAndChequeraPagoIdIsNull("approved");
    }
}
