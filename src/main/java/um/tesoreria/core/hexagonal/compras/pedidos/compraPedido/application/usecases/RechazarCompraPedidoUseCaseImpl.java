package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.RechazarCompraPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;

@Component
@RequiredArgsConstructor
public class RechazarCompraPedidoUseCaseImpl implements RechazarCompraPedidoUseCase {

    private final CompraPedidoRepository compraPedidoRepository;

    @Override
    public CompraPedido rechazar(Integer compraPedidoId) {
        CompraPedido pedido = compraPedidoRepository.findById(compraPedidoId)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
        pedido.rechazar();
        return compraPedidoRepository.update(pedido)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
    }

}
