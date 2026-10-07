package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in.CreateCompraPedidoItemsUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in.DeleteCompraPedidoItemsByPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in.GetCompraPedidoItemsByPedidoUseCase;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompraPedidoItemService {

    private final CreateCompraPedidoItemsUseCase createCompraPedidoItemsUseCase;
    private final GetCompraPedidoItemsByPedidoUseCase getCompraPedidoItemsByPedidoUseCase;
    private final DeleteCompraPedidoItemsByPedidoUseCase deleteCompraPedidoItemsByPedidoUseCase;

    /**
     * Reemplaza el detalle completo de un pedido: borra los ítems actuales y graba los
     * nuevos, preservando el orden recibido.
     */
    @Transactional
    public void reemplazarItems(Integer compraPedidoId, List<CompraPedidoItem> items) {
        deleteCompraPedidoItemsByPedidoUseCase.deleteByPedido(compraPedidoId);
        if (items != null && !items.isEmpty()) {
            items.forEach(item -> item.setCompraPedidoId(compraPedidoId));
            createCompraPedidoItemsUseCase.crear(items);
        }
    }

    public List<CompraPedidoItem> getByPedido(Integer compraPedidoId) {
        return getCompraPedidoItemsByPedidoUseCase.getByPedido(compraPedidoId);
    }

}
