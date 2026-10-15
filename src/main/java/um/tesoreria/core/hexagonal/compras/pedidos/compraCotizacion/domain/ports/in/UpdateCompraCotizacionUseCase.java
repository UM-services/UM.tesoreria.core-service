package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;

/** Edición de un proceso de cotización mientras sea borrador. */
public interface UpdateCompraCotizacionUseCase {

    CompraCotizacion actualizar(Long compraCotizacionId, CompraCotizacion proceso);
}
