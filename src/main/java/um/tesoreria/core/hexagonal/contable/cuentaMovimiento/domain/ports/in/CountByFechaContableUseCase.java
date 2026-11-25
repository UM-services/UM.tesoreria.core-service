package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import java.time.OffsetDateTime;

public interface CountByFechaContableUseCase {

    /** Cantidad de líneas con {@code desde <= fechaContable < hasta}. */
    long countByFechaContable(OffsetDateTime desde, OffsetDateTime hasta);
}
