package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;

public interface RegistrarCompraPedidoHistorialUseCase {

    CompraPedidoHistorial registrar(Integer compraPedidoId, String estado, Integer usuarioId, String observacion);

}
