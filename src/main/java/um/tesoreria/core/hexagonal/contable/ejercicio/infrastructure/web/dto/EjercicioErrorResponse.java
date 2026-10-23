package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.web.dto;

/** VB6 necesita un cuerpo no vacío para reconocer un rechazo; codigo identifica la regla incumplida. */
public record EjercicioErrorResponse(int status, String error, String codigo, String message) {
}
