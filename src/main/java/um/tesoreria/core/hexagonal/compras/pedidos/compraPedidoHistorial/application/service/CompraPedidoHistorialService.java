package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.in.ListCompraPedidoHistorialUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.in.RegistrarCompraPedidoHistorialUseCase;

import java.util.List;

/**
 * Fachada de la línea de tiempo del pedido de compra.
 */
@Service
@RequiredArgsConstructor
public class CompraPedidoHistorialService {

    private final RegistrarCompraPedidoHistorialUseCase registrarCompraPedidoHistorialUseCase;
    private final ListCompraPedidoHistorialUseCase listCompraPedidoHistorialUseCase;

    public CompraPedidoHistorial registrar(Integer compraPedidoId, String estado, Integer usuarioId, String observacion) {
        return registrarCompraPedidoHistorialUseCase.registrar(compraPedidoId, estado, usuarioId, observacion);
    }

    public List<CompraPedidoHistorial> listar(Integer compraPedidoId) {
        return listCompraPedidoHistorialUseCase.listar(compraPedidoId);
    }

}
