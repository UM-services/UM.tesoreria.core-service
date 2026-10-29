package um.tesoreria.core.hexagonal.contable.bancaria.infrastructure.web.dto;

/**
 * VB6 reconoce un rechazo por la clave status del cuerpo (las respuestas exitosas no la tienen); codigo identifica
 * la regla incumplida.
 */
public record BancariaErrorResponse(int status, String error, String codigo, String message) {
}
