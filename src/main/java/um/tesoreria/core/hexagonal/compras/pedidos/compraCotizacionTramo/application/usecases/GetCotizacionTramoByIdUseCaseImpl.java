package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.GetCotizacionTramoByIdUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out.CompraCotizacionTramoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetCotizacionTramoByIdUseCaseImpl implements GetCotizacionTramoByIdUseCase {

    private final CompraCotizacionTramoRepository repository;

    @Override
    public Optional<CompraCotizacionTramo> getById(Long cotizacionTramoId) {
        return repository.findById(cotizacionTramoId);
    }

}
