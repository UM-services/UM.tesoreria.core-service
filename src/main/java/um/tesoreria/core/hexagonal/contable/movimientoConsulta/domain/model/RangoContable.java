package um.tesoreria.core.hexagonal.contable.movimientoConsulta.domain.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * Rango de días contables. Toma el día de calendario de cada fecha tal como llega, con su offset, y acepta
 * cualquier hora. La conexión usa {@code serverTimezone=UTC}: un {@code datetime} a las 00:00 llega como 00:00Z.
 * Por eso el rango a consultar es [00:00Z de {@code desde}, 00:00Z del día siguiente a {@code hasta}).
 */
public record RangoContable(LocalDate desde, LocalDate hasta) {

    /** Primer día que acepta un {@code datetime} de MySQL. */
    public static final LocalDate MINIMO = LocalDate.of(1000, 1, 1);

    /** Último {@code hasta} posible: {@link #fin()} es el día siguiente y debe seguir siendo un {@code datetime} válido. */
    public static final LocalDate MAXIMO = LocalDate.of(9999, 12, 30);

    public static RangoContable deFechas(OffsetDateTime desde, OffsetDateTime hasta) {
        return new RangoContable(desde.toLocalDate(), hasta.toLocalDate());
    }

    public boolean fueraDeRango() {
        return desde.isBefore(MINIMO) || desde.isAfter(MAXIMO) || hasta.isBefore(MINIMO) || hasta.isAfter(MAXIMO);
    }

    /** Días del rango, contando los dos extremos. */
    public long dias() {
        return ChronoUnit.DAYS.between(desde, hasta) + 1;
    }

    public boolean invertido() {
        return desde.isAfter(hasta);
    }

    /** Primer instante incluido. */
    public OffsetDateTime inicio() {
        return desde.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    /** Primer instante excluido. */
    public OffsetDateTime fin() {
        return hasta.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
    }
}
