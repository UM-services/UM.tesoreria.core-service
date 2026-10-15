package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.in.RegistrarCompraPedidoHistorialUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.ports.out.CompraPedidoHistorialRepository;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RegistrarCompraPedidoHistorialUseCaseImpl implements RegistrarCompraPedidoHistorialUseCase {

    private final CompraPedidoHistorialRepository compraPedidoHistorialRepository;

    @Override
    public CompraPedidoHistorial registrar(Integer compraPedidoId, String estado, Integer usuarioId, String observacion) {
        return compraPedidoHistorialRepository.save(new CompraPedidoHistorial(
                null, compraPedidoId, estado, usuarioId, observacion, LocalDateTime.now()));
    }

}
