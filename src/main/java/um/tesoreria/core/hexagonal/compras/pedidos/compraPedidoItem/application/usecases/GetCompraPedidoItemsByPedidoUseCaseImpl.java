package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in.GetCompraPedidoItemsByPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.out.CompraPedidoItemRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetCompraPedidoItemsByPedidoUseCaseImpl implements GetCompraPedidoItemsByPedidoUseCase {

    private final CompraPedidoItemRepository compraPedidoItemRepository;

    @Override
    public List<CompraPedidoItem> getByPedido(Integer compraPedidoId) {
        return compraPedidoItemRepository.findByCompraPedidoId(compraPedidoId);
    }

}
