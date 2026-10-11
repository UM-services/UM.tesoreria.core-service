package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto;

/**
 * Mínimo de cotizaciones resuelto para un monto de un ejercicio
 * ({@code GET /compraCotizacionTramo/minimo}). {@code 409} con {@code COTIZACIONES_NO_CONFIGURADAS}
 * cuando la configuración no permite resolverlo.
 */
public record CompraCotizacionMinimoResponse(Integer cantidadMinima) {
}
