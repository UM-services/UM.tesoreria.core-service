package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;

public record CompraCotizacionItemRequest(
        Integer compraPedidoItemId,
        Integer orden,
        BigDecimal cantidad) {
}
