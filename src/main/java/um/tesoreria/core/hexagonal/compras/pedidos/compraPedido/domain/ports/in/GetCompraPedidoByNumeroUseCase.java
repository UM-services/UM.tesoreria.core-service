package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;

import java.util.Optional;

public interface GetCompraPedidoByNumeroUseCase {

    Optional<CompraPedido> getByNumero(String numero);

}
