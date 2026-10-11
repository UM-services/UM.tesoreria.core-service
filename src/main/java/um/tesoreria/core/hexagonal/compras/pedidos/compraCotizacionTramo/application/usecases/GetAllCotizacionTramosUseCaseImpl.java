package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.GetAllCotizacionTramosUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out.CompraCotizacionTramoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GetAllCotizacionTramosUseCaseImpl implements GetAllCotizacionTramosUseCase {

    private final CompraCotizacionTramoRepository repository;

    @Override
    public List<CompraCotizacionTramo> getAll() {
        return repository.findAll();
    }

}
