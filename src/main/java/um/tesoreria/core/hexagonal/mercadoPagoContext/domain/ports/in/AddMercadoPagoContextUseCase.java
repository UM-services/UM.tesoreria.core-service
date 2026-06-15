package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;

public interface AddMercadoPagoContextUseCase {
    MercadoPagoContext add(MercadoPagoContext mercadoPagoContext);
}
