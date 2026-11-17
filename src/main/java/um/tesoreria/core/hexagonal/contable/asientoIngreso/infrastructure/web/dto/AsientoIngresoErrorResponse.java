package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.web.dto;

/** Cuerpo de los rechazos del slice: {@code {status, error, codigo, message}}. */
public record AsientoIngresoErrorResponse(int status, String error, String codigo, String message) {
}
