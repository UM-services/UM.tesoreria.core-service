package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import um.tesoreria.core.hexagonal.mercadoPagoContext.domain.model.MercadoPagoContext;
import java.util.List;

public interface FindAllByChequeraCuotaIdAndActivoUseCase {
    List<MercadoPagoContext> findAllByChequeraCuotaIdAndActivo(Long chequeraCuotaId, Byte activo);
}
