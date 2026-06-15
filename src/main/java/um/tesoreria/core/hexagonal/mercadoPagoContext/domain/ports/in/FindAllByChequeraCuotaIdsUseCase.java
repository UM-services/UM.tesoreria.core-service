package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import java.util.List;

public interface FindAllByChequeraCuotaIdsUseCase {
    List<MercadoPagoContext> findAllByChequeraCuotaIds(List<Long> chequeraCuotaIds);
}
