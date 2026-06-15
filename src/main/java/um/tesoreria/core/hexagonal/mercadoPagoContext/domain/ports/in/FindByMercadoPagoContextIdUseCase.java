package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;

public interface FindByMercadoPagoContextIdUseCase {
    MercadoPagoContext findByMercadoPagoContextId(Long mercadoPagoContextId);
}
