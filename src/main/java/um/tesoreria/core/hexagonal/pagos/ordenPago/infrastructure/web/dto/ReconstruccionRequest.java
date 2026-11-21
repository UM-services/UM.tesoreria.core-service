package um.tesoreria.core.hexagonal.pagos.ordenPago.infrastructure.web.dto;

/**
 * Parámetros de la reconstrucción de traza. {@code forzar} habilita reescribir asientos que
 * la orden de pago comparte con otros comprobantes.
 */
public record ReconstruccionRequest(Boolean forzar) {
}
