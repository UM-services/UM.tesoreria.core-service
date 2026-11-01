package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import java.time.OffsetDateTime;

public interface ExistsByFechaContableBetweenUseCase {
    boolean existsByFechaContableBetween(OffsetDateTime desde, OffsetDateTime hasta);
}
