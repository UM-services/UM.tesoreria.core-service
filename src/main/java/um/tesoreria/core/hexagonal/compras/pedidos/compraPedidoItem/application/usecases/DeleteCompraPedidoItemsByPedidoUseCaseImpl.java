package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in.DeleteCompraPedidoItemsByPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.out.CompraPedidoItemRepository;

@Component
@RequiredArgsConstructor
public class DeleteCompraPedidoItemsByPedidoUseCaseImpl implements DeleteCompraPedidoItemsByPedidoUseCase {

    private final CompraPedidoItemRepository compraPedidoItemRepository;

    @Override
    public void deleteByPedido(Integer compraPedidoId) {
        compraPedidoItemRepository.deleteByCompraPedidoId(compraPedidoId);
    }

}
