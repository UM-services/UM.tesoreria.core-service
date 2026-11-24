package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;

import java.time.OffsetDateTime;
import java.util.List;

public interface FindLineasByFechaContableUseCase {

    /**
     * Líneas con {@code desde <= fechaContable < hasta}, ordenadas por fecha, orden, ítem e id. Devuelve como mucho
     * {@code limite} líneas: quien pide {@code tope + 1} sabe si el rango supera el tope sin cargarlo entero.
     */
    List<LineaMovimiento> findLineasByFechaContable(OffsetDateTime desde, OffsetDateTime hasta, int limite);
}
