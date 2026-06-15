package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.SaveAllMercadoPagoContextsUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SaveAllMercadoPagoContextsUseCaseImpl implements SaveAllMercadoPagoContextsUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public List<MercadoPagoContext> saveAll(List<MercadoPagoContext> mercadoPagoContexts) {
        return repository.saveAll(mercadoPagoContexts);
    }
}
