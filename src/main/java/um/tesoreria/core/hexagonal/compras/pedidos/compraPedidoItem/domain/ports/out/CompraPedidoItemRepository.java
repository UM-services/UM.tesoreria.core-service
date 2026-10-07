package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;

import java.util.List;

public interface CompraPedidoItemRepository {

    List<CompraPedidoItem> saveAll(List<CompraPedidoItem> items);

    List<CompraPedidoItem> findByCompraPedidoId(Integer compraPedidoId);

    void deleteByCompraPedidoId(Integer compraPedidoId);

}
