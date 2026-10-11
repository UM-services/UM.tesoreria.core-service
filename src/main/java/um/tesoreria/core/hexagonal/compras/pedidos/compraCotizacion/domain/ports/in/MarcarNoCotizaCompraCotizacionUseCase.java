package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;

/** Marca que un proveedor declinó cotizar. */
public interface MarcarNoCotizaCompraCotizacionUseCase {

    CompraCotizacionProveedor marcarNoCotiza(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                             String observacion);
}
