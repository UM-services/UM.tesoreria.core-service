package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import java.util.List;

public interface FindAllActiveToChangeUseCase {
    List<Long> findAllActiveToChange();
}
