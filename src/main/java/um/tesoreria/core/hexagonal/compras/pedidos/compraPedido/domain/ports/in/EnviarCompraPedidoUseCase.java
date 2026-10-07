package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

public interface EnviarCompraPedidoUseCase {

    CompraPedido enviar(Integer compraPedidoId, String numero);

}
