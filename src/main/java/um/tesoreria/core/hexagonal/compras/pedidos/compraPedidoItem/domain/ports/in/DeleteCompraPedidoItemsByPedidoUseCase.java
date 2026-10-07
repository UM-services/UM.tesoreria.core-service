package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in;

public interface DeleteCompraPedidoItemsByPedidoUseCase {

    void deleteByPedido(Integer compraPedidoId);

}
