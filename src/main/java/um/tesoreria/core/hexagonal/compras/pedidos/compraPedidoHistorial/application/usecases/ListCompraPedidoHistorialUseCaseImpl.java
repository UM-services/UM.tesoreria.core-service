package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.in.ListCompraPedidoHistorialUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.out.CompraPedidoHistorialRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCompraPedidoHistorialUseCaseImpl implements ListCompraPedidoHistorialUseCase {

    private final CompraPedidoHistorialRepository compraPedidoHistorialRepository;

    @Override
    public List<CompraPedidoHistorial> listar(Integer compraPedidoId) {
        return compraPedidoHistorialRepository.findByPedido(compraPedidoId);
    }

}
