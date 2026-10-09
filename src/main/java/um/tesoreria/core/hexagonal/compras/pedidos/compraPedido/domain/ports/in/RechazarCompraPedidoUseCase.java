package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

public interface RechazarCompraPedidoUseCase {

    CompraPedido rechazar(Integer compraPedidoId, Integer autorizanteId, String motivo);

}
