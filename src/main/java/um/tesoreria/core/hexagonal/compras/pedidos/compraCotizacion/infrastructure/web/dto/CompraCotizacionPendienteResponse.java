package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto;

import java.math.BigDecimal;

public record CompraCotizacionPendienteResponse(
        Integer compraPedidoItemId,
        Integer compraPedidoId,
        String pedidoNumero,
        Integer dependenciaId,
        Integer orden,
        BigDecimal cantidad,
        String unidad,
        String descripcion,
        String especificaciones,
        String referenciaWeb) {
}
