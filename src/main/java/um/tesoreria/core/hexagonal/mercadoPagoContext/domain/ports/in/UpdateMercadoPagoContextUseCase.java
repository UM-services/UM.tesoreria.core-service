package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;

public interface UpdateMercadoPagoContextUseCase {
    MercadoPagoContext update(MercadoPagoContext newMercadoPagoContext, Long mercadoPagoContextId);
}
