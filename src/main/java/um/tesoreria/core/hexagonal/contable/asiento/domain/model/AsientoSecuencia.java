package um.tesoreria.core.hexagonal.contable.asiento.domain.model;

import java.time.OffsetDateTime;

/**
 * Último orden contable reservado para una fecha. Es la fuente autoritativa de la numeración:
 * se reserva antes de escribir {@code movcon} y se entrega {@code ultimoOrden + 1}.
 */
public record AsientoSecuencia(OffsetDateTime fecha, int ultimoOrden) {
}
