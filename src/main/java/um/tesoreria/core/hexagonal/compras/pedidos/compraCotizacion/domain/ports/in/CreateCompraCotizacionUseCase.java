package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;

/** Alta de un proceso de cotización en borrador (ítems combinados + proveedores). */
public interface CreateCompraCotizacionUseCase {

    CompraCotizacion crear(CompraCotizacion proceso);
}
