package um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.ports.out;

import um.tesoreria.core.hexagonal.mercadoPagoContextHistory.domain.model.MercadoPagoContextHistory;
import um.tesoreria.core.model.MercadoPagoContext;

public interface MercadoPagoContextHistoryRepository {

    MercadoPagoContextHistory create(MercadoPagoContext mercadoPagoContext);

}
