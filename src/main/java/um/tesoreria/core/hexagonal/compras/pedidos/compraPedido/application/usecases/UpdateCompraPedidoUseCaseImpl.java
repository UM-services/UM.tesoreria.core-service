package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.UpdateCompraPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;

@Component
@RequiredArgsConstructor
public class UpdateCompraPedidoUseCaseImpl implements UpdateCompraPedidoUseCase {

    private final CompraPedidoRepository compraPedidoRepository;

    @Override
    public CompraPedido actualizar(Integer compraPedidoId, CompraPedido datos) {
        CompraPedido pedido = compraPedidoRepository.findById(compraPedidoId)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
        pedido.actualizarDatos(datos);
        return compraPedidoRepository.update(pedido)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
    }

}
