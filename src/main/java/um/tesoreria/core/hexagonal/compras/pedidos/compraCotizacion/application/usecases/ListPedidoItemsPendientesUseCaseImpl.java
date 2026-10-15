package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.ListPedidoItemsPendientesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.PedidoItemsPendientesPort;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListPedidoItemsPendientesUseCaseImpl implements ListPedidoItemsPendientesUseCase {

    private final PedidoItemsPendientesPort pedidoItemsPendientesPort;

    @Override
    public List<CompraCotizacionPendiente> listar(Integer ejercicioId, List<Integer> dependenciaIds) {
        return pedidoItemsPendientesPort.listar(ejercicioId, dependenciaIds);
    }
}
