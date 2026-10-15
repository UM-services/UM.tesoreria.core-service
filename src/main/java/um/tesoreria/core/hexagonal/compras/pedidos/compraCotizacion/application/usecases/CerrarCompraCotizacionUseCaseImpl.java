package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.exception.CompraCotizacionException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.CerrarCompraCotizacionUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

@Component
@RequiredArgsConstructor
public class CerrarCompraCotizacionUseCaseImpl implements CerrarCompraCotizacionUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public CompraCotizacion cerrar(Long compraCotizacionId) {
        CompraCotizacion proceso = repository.findById(compraCotizacionId)
                .orElseThrow(() -> new CompraCotizacionException(compraCotizacionId));
        proceso.cerrar();
        return repository.updateHeader(proceso);
    }
}
