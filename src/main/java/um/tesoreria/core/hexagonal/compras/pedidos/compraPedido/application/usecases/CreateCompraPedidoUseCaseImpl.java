package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.application.exception.CompraPedidoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.EjercicioActual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.in.CreateCompraPedidoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.CompraPedidoRepository;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.EjercicioActualPort;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CreateCompraPedidoUseCaseImpl implements CreateCompraPedidoUseCase {

    private final CompraPedidoRepository compraPedidoRepository;
    private final EjercicioActualPort ejercicioActualPort;

    @Override
    public CompraPedido crear(CompraPedido pedido) {
        if (pedido.getSolicitanteId() == null || pedido.getDependenciaId() == null) {
            throw new CompraPedidoException("El pedido requiere solicitante y dependencia");
        }
        LocalDateTime fecha = pedido.getFecha() != null ? pedido.getFecha() : LocalDateTime.now();
        pedido.setFecha(fecha);
        if (pedido.getEjercicioId() == null) {
            pedido.setEjercicioId(ejercicioActualPort.findEjercicioActual(fecha)
                    .map(EjercicioActual::ejercicioId)
                    .orElseThrow(() -> new CompraPedidoException("No hay un ejercicio abierto para la fecha " + fecha)));
        }
        // Nace como borrador, sin número: el correlativo se reserva al enviar.
        pedido.setEstado(CompraPedidoEstado.BORRADOR);
        pedido.setNumero(null);
        return compraPedidoRepository.create(pedido);
    }

}
