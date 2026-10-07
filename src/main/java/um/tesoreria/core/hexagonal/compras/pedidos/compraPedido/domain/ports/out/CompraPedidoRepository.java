package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoCriteria;

import java.util.List;
import java.util.Optional;

public interface CompraPedidoRepository {

    CompraPedido create(CompraPedido pedido);

    Optional<CompraPedido> update(CompraPedido pedido);

    Optional<CompraPedido> findById(Integer compraPedidoId);

    Optional<CompraPedido> findByNumero(String numero);

    List<CompraPedido> findByCriteria(CompraPedidoCriteria criteria);

}
