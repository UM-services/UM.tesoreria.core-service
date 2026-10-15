package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.GetCompraPedidoByNumeroUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCompraPedidoByNumeroUseCaseImpl implements GetCompraPedidoByNumeroUseCase {

    private final CompraPedidoRepository compraPedidoRepository;

    @Override
    public Optional<CompraPedido> getByNumero(String numero) {
        return compraPedidoRepository.findByNumero(numero);
    }

}
