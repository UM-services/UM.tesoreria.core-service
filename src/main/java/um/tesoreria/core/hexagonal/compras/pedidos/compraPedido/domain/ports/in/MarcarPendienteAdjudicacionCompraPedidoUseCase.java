package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

/** Pasa el pedido a {@code PENDIENTE_ADJUDICACION} cuando su proceso quedó recomendado. */
public interface MarcarPendienteAdjudicacionCompraPedidoUseCase {

    CompraPedido marcar(Integer compraPedidoId);
}
