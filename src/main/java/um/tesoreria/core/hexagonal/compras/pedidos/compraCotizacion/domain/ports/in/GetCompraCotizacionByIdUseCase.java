package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;

import java.util.Optional;

public interface GetCompraCotizacionByIdUseCase {

    Optional<CompraCotizacion> getById(Long compraCotizacionId);
}
