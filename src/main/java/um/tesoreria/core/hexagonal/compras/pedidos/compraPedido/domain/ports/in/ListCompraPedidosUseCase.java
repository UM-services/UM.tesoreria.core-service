package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoCriteria;

import java.util.List;

public interface ListCompraPedidosUseCase {

    List<CompraPedido> listar(CompraPedidoCriteria criteria);

}
