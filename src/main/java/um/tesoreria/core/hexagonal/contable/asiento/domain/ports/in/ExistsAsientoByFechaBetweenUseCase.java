package um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in;

import java.time.OffsetDateTime;

public interface ExistsAsientoByFechaBetweenUseCase {
    boolean existsByFechaBetween(OffsetDateTime desde, OffsetDateTime hasta);
}
