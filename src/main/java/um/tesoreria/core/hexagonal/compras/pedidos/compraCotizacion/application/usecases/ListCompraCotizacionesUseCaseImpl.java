package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in.ListCompraCotizacionesUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.out.CompraCotizacionRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ListCompraCotizacionesUseCaseImpl implements ListCompraCotizacionesUseCase {

    private final CompraCotizacionRepository repository;

    @Override
    public List<CompraCotizacion> listar(CompraCotizacionCriteria criteria) {
        return repository.findByCriteria(criteria);
    }
}
