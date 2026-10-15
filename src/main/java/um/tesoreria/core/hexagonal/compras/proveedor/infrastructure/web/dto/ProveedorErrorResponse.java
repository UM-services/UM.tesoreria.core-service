package um.tesoreria.core.hexagonal.compras.proveedor.infrastructure.web.dto;

/**
 * Cuerpo de las respuestas de error de proveedor. Lleva siempre {@code status} (VB6 lo usa para saber si la
 * llamada falló) y {@code message} con la explicación para mostrar al usuario. No depende de
 * {@code server.error.include-message}, que por defecto oculta el mensaje.
 */
public record ProveedorErrorResponse(int status, String error, String message) {
}