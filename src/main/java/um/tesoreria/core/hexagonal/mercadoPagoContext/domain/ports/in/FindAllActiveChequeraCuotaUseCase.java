package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import java.util.List;

public interface FindAllActiveChequeraCuotaUseCase {
    List<Long> findAllActiveChequeraCuota();
}
