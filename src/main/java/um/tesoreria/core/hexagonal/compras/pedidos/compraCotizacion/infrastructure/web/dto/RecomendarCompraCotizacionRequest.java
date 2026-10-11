package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

public record RecomendarCompraCotizacionRequest(
        Long compraCotizacionProveedorId,
        String fundamento) {
}
