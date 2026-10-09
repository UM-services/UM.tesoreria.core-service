package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;

import java.util.List;

public interface ListCompraPedidoHistorialUseCase {

    List<CompraPedidoHistorial> listar(Integer compraPedidoId);

}
