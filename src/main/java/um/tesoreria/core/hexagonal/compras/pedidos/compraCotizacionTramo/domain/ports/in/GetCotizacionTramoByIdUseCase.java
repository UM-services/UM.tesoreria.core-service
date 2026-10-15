package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

import java.util.Optional;

public interface GetCotizacionTramoByIdUseCase {

    Optional<CompraCotizacionTramo> getById(Long cotizacionTramoId);

}
