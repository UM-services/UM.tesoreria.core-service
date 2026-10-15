package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;

import java.util.List;

public interface CompraPedidoHistorialRepository {

    CompraPedidoHistorial save(CompraPedidoHistorial historial);

    List<CompraPedidoHistorial> findByPedido(Integer compraPedidoId);

}
