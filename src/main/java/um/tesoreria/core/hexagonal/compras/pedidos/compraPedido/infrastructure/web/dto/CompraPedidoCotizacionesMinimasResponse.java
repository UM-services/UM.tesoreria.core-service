package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

/**
 * Mínimo de cotizaciones requerido por un pedido (snapshot al estimar o cálculo vigente).
 * {@code cantidadMinima} puede ser {@code null} si la configuración no permite resolverlo.
 */
public record CompraPedidoCotizacionesMinimasResponse(Integer compraPedidoId, Integer cantidadMinima) {
}
