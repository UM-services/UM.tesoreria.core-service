package um.tesoreria.core.model;

/**
 * Página solicitada en el cuerpo de un POST. Reemplaza los parámetros de query
 * {@code page}/{@code size}; si viajan nulos, el endpoint aplica sus valores por defecto.
 */
public record PageRequest(Integer page, Integer size) {
}
