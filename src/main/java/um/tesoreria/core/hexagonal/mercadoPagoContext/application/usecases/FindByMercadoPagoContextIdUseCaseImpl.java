package um.tesoreria.core.hexagonal.mercadoPagoContext.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception.MercadoPagoContextException;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in.FindByMercadoPagoContextIdUseCase;
import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.out.MercadoPagoContextRepository;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class FindByMercadoPagoContextIdUseCaseImpl implements FindByMercadoPagoContextIdUseCase {

    private final MercadoPagoContextRepository repository;

    @Override
    public MercadoPagoContext findByMercadoPagoContextId(Long mercadoPagoContextId) {
        return Objects.requireNonNull(repository.findByMercadoPagoContextId(mercadoPagoContextId))
                .orElseThrow(() -> new MercadoPagoContextException("No se encontró MPContext para mercadoPagoContextId", mercadoPagoContextId));
    }
}
