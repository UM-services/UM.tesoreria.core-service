package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

public interface UpdateCotizacionTramoUseCase {

    CompraCotizacionTramo update(CompraCotizacionTramo tramo, Long cotizacionTramoId);

}
