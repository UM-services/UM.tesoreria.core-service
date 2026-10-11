package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;

public record CompraCotizacionProveedorItemResponse(
        Long compraCotizacionProveedorItemId,
        Long compraCotizacionItemId,
        Boolean cotiza,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca,
        String observacion) {
}
