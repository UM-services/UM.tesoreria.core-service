package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.entity.CompraFormalizacionItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.persistence.repository.JpaCompraFormalizacionItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity.CompraPedidoItemEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.repository.JpaCompraPedidoItemRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.out.CompraRecepcionPedidoPort;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Pedido de origen de cada ítem de la Orden de Compra: encadena
 * {@code compra_formalizacion_item -> compra_pedido_item} para resolver qué pedidos se completan
 * con la entrega. Dos lecturas por repositorio JPA, sin SQL escrito a mano.
 */
@Component
@RequiredArgsConstructor
public class JpaCompraRecepcionPedidoAdapter implements CompraRecepcionPedidoPort {

    private final JpaCompraFormalizacionItemRepository formalizacionItemRepository;
    private final JpaCompraPedidoItemRepository pedidoItemRepository;

    @Override
    public Map<Long, Integer> pedidoIdsByFormalizacionItem(Long compraFormalizacionId) {
        var items = formalizacionItemRepository.findByCompraFormalizacionIdOrderByOrden(compraFormalizacionId);
        Set<Integer> pedidoItemIds = new LinkedHashSet<>();
        for (CompraFormalizacionItemEntity item : items) {
            if (item.getCompraPedidoItemId() != null) {
                pedidoItemIds.add(item.getCompraPedidoItemId());
            }
        }
        Map<Integer, Integer> pedidoPorItem = new LinkedHashMap<>();
        for (CompraPedidoItemEntity pedidoItem : pedidoItemRepository.findAllById(pedidoItemIds)) {
            pedidoPorItem.put(pedidoItem.getCompraPedidoItemId(), pedidoItem.getCompraPedidoId());
        }
        Map<Long, Integer> pedidos = new LinkedHashMap<>();
        for (CompraFormalizacionItemEntity item : items) {
            Integer pedidoId = pedidoPorItem.get(item.getCompraPedidoItemId());
            if (pedidoId != null) {
                pedidos.put(item.getCompraFormalizacionItemId(), pedidoId);
            }
        }
        return pedidos;
    }

}
