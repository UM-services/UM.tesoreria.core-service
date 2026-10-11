package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;

public record CompraCotizacionProveedorItemRequest(
        Long compraCotizacionItemId,
        Boolean cotiza,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String marca,
        String observacion) {
}
