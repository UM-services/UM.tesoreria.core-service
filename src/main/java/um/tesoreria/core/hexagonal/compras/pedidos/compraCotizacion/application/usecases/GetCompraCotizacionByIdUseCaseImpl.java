package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.GetCompraCotizacionByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCompraCotizacionByIdUseCaseImpl implements GetCompraCotizacionByIdUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public Optional<CompraCotizacion> getById(Long compraCotizacionId) {
        return repository.findById(compraCotizacionId);
    }
}
