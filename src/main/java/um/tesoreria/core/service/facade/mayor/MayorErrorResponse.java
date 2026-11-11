package um.tesoreria.core.service.facade.mayor;

/** Cuerpo de los rechazos de negocio de la consulta del mayor; {@code codigo} identifica la regla incumplida. */
public record MayorErrorResponse(int status, String error, String codigo, String message) {
}