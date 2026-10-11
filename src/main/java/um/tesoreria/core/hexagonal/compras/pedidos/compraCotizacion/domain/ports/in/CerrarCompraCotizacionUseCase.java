package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;

public interface CerrarCompraCotizacionUseCase {

    CompraCotizacion cerrar(Long compraCotizacionId);
}
