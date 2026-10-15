package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.ListCompraPedidosUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCompraPedidosUseCaseImpl implements ListCompraPedidosUseCase {

    private final CompraPedidoRepository compraPedidoRepository;

    @Override
    public List<CompraPedido> listar(CompraPedidoCriteria criteria) {
        return compraPedidoRepository.findByCriteria(criteria);
    }

}
