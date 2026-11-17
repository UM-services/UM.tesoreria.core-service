package um.tesoreria.core.hexagonal.contable.asientoIngreso.application.service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * El legacy (y el core) tratan las fechas contables como día calendario a las 00:00 UTC
 * ({@code date2datetimeIso} -> {@code yyyy-MM-ddT00:00Z}). Normalizar acá evita que un cliente
 * que mande {@code -03:00} consulte otra fila.
 */
public final class FechasContables {

    private FechasContables() {
    }

    public static OffsetDateTime normalizar(OffsetDateTime fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha contable es obligatoria");
        }
        return fecha.withOffsetSameInstant(ZoneOffset.UTC)
                .toLocalDate()
                .atStartOfDay()
                .atOffset(ZoneOffset.UTC);
    }

    public static long diasEntre(OffsetDateTime desde, OffsetDateTime hasta) {
        return ChronoUnit.DAYS.between(desde.toLocalDate(), hasta.toLocalDate()) + 1;
    }

}
