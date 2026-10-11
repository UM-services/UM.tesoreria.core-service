package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.RecomendarCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

@Component
@RequiredArgsConstructor
public class RecomendarCompraCotizacionUseCaseImpl implements RecomendarCompraCotizacionUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public CompraCotizacion recomendar(Long compraCotizacionId, Long compraCotizacionProveedorId, String fundamento) {
        CompraCotizacion proceso = repository.findById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
        long respondidas = proceso.getProveedores() == null ? 0
                : proceso.getProveedores().stream().filter(CompraCotizacionProveedor::esRespondida).count();
        proceso.recomendar(compraCotizacionProveedorId, fundamento, respondidas);
        return repository.updateHeader(proceso);
    }
}
