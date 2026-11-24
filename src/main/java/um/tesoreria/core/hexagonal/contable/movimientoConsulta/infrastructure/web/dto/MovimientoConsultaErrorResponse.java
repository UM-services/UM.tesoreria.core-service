package um.tesoreria.core.hexagonal.contable.movimientoConsulta.infrastructure.web.dto;

/** Cuerpo de los rechazos de negocio; {@code codigo} identifica la regla incumplida. */
public record MovimientoConsultaErrorResponse(int status, String error, String codigo, String message) {
}
