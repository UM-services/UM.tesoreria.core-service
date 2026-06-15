package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.AddMercadoPagoContextUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

@Component
@RequiredArgsConstructor
public class AddMercadoPagoContextUseCaseImpl implements AddMercadoPagoContextUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public MercadoPagoContext add(MercadoPagoContext mercadoPagoContext) {
        return repository.save(mercadoPagoContext);
    }
}
