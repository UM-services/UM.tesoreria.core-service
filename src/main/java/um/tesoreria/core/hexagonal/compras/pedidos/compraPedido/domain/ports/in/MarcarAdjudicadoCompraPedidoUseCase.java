package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

/** Pasa el pedido a {@code ADJUDICADO} cuando la autoridad adjudicó su cotización recomendada. */
public interface MarcarAdjudicadoCompraPedidoUseCase {

    CompraPedido marcar(Integer compraPedidoId);
}
