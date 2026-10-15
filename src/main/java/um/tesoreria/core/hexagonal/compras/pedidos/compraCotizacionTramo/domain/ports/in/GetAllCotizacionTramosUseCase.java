package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

import java.util.List;

public interface GetAllCotizacionTramosUseCase {

    List<CompraCotizacionTramo> getAll();

}
