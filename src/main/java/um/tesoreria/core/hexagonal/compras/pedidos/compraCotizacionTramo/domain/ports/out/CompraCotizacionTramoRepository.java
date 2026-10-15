package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;

import java.util.List;
import java.util.Optional;

public interface CompraCotizacionTramoRepository {

    Optional<CompraCotizacionTramo> findById(Long cotizacionTramoId);

    List<CompraCotizacionTramo> findAll();

    CompraCotizacionTramo save(CompraCotizacionTramo tramo);

    boolean existsById(Long cotizacionTramoId);

    void deleteById(Long cotizacionTramoId);

}
