package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.model.MercadoPagoContextHistory;
import um.tesoreria.core.model.MercadoPagoContext;

public interface CreateMercadoPagoContextHistoryUseCase {

    MercadoPagoContextHistory createMercadoPagoContextHistory(MercadoPagoContext mercadoPagoContext);

}
