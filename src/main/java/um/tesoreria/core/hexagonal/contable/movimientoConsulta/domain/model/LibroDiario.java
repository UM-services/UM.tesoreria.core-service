package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

/** Libro Diario de un rango: asientos en orden y totales del rango. */
public record LibroDiario(
        OffsetDateTime desde,
        OffsetDateTime hasta,
        List<AsientoDiario> asientos,
        BigDecimal totalDebe,
        BigDecimal totalHaber) {
}
