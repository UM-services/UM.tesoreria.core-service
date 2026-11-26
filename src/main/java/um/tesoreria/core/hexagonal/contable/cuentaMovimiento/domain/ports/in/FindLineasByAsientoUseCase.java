package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;

import java.time.OffsetDateTime;
import java.util.List;

public interface FindLineasByAsientoUseCase {

    /**
     * Líneas del asiento {@code ordenContable} con {@code desde <= fechaContable < hasta}, ordenadas por ítem e id,
     * sin relaciones.
     */
    List<LineaMovimiento> findLineasByAsiento(OffsetDateTime desde, OffsetDateTime hasta, Integer ordenContable);
}
