package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;

import java.util.UUID;

public interface FindActiveByReservaVacanteIdUseCase {
    MercadoPagoContext findActiveByReservaVacanteId(UUID reservaVacanteId);
}
