package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.in.CreateCompraPedidoItemsUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.ports.out.CompraPedidoItemRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreateCompraPedidoItemsUseCaseImpl implements CreateCompraPedidoItemsUseCase {

    private final CompraPedidoItemRepository compraPedidoItemRepository;

    @Override
    public List<CompraPedidoItem> crear(List<CompraPedidoItem> items) {
        return compraPedidoItemRepository.saveAll(items);
    }

}
