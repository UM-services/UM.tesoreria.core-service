package um.tesoreria.core.hexagonal.contable.ejercicio.domain.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

/**
 * La conexión usa serverTimezone=UTC: un datetime a las 00:00 llega como 00:00Z. Pasarlo a
 * Buenos Aires correría el día un lugar hacia atrás; por eso todo se convierte en UTC.
 */
public final class EjercicioFechas {

    private EjercicioFechas() {
    }

    public static LocalDate aDia(OffsetDateTime fecha) {
        return fecha == null ? null : fecha.withOffsetSameInstant(ZoneOffset.UTC).toLocalDate();
    }

    public static OffsetDateTime aInicioDeDia(LocalDate dia) {
        return dia == null ? null : dia.atStartOfDay().atOffset(ZoneOffset.UTC);
    }

    /** Último instante (a segundo entero, sin redondeo de datetime) del día. */
    public static OffsetDateTime aFinDeDia(LocalDate dia) {
        return dia == null ? null : dia.atTime(23, 59, 59).atOffset(ZoneOffset.UTC);
    }
}
