package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

public interface AutorizarCompraPedidoUseCase {

    CompraPedido autorizar(Integer compraPedidoId, Integer autorizanteId);

}
