package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

import java.util.Optional;

public interface GetCompraPedidoByIdUseCase {

    Optional<CompraPedido> getById(Integer compraPedidoId);

}
