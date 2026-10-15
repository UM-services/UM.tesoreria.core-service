package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out;

import java.util.Map;

/**
 * Pedido de compra de origen de cada ítem de la Orden de Compra. Consulta de lectura sobre las
 * tablas de compras (trazabilidad {@code compra_formalizacion_item -> compra_pedido_item}).
 */
public interface CompraRecepcionPedidoPort {

    Map<Long, Integer> pedidoIdsByFormalizacionItem(Long compraFormalizacionId);

}
