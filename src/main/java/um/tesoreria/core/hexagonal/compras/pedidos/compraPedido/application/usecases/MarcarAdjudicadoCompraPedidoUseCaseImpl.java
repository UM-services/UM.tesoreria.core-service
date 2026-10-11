package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.MarcarAdjudicadoCompraPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;

@Component
@RequiredArgsConstructor
public class MarcarAdjudicadoCompraPedidoUseCaseImpl implements MarcarAdjudicadoCompraPedidoUseCase {

    private final CompraPedidoRepository compraPedidoRepository;

    @Override
    public CompraPedido marcar(Integer compraPedidoId) {
        CompraPedido pedido = compraPedidoRepository.findById(compraPedidoId)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
        pedido.marcarAdjudicado();
        return compraPedidoRepository.update(pedido)
                .orElseThrow(() -> new CompraPedidoException(compraPedidoId));
    }
}
