package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionCriteria;

import java.util.List;

public interface ListCompraCotizacionesUseCase {

    List<CompraCotizacion> listar(CompraCotizacionCriteria criteria);
}
