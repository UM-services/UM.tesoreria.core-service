package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Movimientos Contables de un rango: líneas sueltas en orden de fecha, orden e ítem, y totales del rango. */
public record MovimientosContables(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        List<LineaMovimientoContable> lineas,
        BigDecimal totalDebe,
        BigDecimal totalHaber) {
}
