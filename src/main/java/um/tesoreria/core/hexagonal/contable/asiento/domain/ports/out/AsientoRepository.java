package um.tesoreria.core.hexagonal.contable.asiento.domain.ports.out;

import java.time.OffsetDateTime;

public interface AsientoRepository {
    boolean existsByFechaBetween(OffsetDateTime desde, OffsetDateTime hasta);
}
