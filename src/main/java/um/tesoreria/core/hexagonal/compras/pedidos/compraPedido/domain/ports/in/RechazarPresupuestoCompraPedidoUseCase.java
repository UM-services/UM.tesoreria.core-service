package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

public interface RechazarPresupuestoCompraPedidoUseCase {

    CompraPedido rechazar(Integer compraPedidoId, String motivo);

}
