package um.tesoreria.core.hexagonal.mercadoPagoContext.domain.ports.in;

import java.util.List;

/**
 * @deprecated Flujo legado de barrido (90 días). Usar {@code FindRenovacionesUseCase}.
 */
@Deprecated
public interface FindAllActiveToChangeUseCase {
    List<Long> findAllActiveToChange();
}
