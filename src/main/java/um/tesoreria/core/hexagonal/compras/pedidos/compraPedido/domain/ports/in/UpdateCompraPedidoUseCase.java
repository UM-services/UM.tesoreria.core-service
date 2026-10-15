package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

public interface UpdateCompraPedidoUseCase {

    CompraPedido actualizar(Integer compraPedidoId, CompraPedido datos);

}
