package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

/**
 * Rango de días contables. Se toma el día de calendario de cada fecha con su offset; la hora no importa.
 * Las fechas se leen sin pasarlas a UTC: {@code 2025-03-01T22:00-03:00} es el 1 de marzo, no el 2.
 */
public record RangoFechasRequest(
        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE) OffsetDateTime desde,
        @JsonFormat(without = JsonFormat.Feature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE) OffsetDateTime hasta) {
}
