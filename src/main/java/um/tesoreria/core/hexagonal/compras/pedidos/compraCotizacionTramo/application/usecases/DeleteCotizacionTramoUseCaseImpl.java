package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.application.exception.CompraCotizacionTramoException;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in.DeleteCotizacionTramoUseCase;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out.CompraCotizacionTramoRepository;

@Component
@RequiredArgsConstructor
public class DeleteCotizacionTramoUseCaseImpl implements DeleteCotizacionTramoUseCase {

    private final CompraCotizacionTramoRepository repository;

    @Override
    public void delete(Long cotizacionTramoId) {
        if (cotizacionTramoId == null) {
            throw new CompraCotizacionTramoException("cotizacionTramoId es requerido");
        }
        if (!repository.existsById(cotizacionTramoId)) {
            throw new CompraCotizacionTramoException(cotizacionTramoId);
        }
        repository.deleteById(cotizacionTramoId);
    }

}
