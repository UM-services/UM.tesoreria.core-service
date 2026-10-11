package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

public record CompraCotizacionProveedorRequest(
        Integer proveedorId,
        String email,
        String observacion) {
}
