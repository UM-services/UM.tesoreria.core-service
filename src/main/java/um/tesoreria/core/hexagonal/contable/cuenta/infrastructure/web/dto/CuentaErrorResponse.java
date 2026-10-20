package um.tesoreria.core.hexagonal.contable.cuenta.infrastructure.web.dto;

/** VB6 necesita un cuerpo no vacío para reconocer un rechazo de la operación. */
public record CuentaErrorResponse(int status, String error, String message) {
}
