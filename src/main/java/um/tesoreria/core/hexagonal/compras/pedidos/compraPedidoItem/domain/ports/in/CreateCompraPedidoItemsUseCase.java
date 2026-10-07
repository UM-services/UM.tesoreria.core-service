package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;

import java.util.List;

public interface CreateCompraPedidoItemsUseCase {

    List<CompraPedidoItem> crear(List<CompraPedidoItem> items);

}
