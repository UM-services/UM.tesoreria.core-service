package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

/** Pasa el pedido a {@code EN_COTIZACION} cuando todos sus ítems integran un proceso enviado. */
public interface MarcarEnCotizacionCompraPedidoUseCase {

    CompraPedido marcar(Integer compraPedidoId);
}
