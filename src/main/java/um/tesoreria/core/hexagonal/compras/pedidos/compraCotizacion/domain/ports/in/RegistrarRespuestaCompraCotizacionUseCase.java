package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;

/** Carga la respuesta de un proveedor (cabecera + precios por ítem). */
public interface RegistrarRespuestaCompraCotizacionUseCase {

    CompraCotizacionProveedor registrarRespuesta(Long compraCotizacionId, Long compraCotizacionProveedorId,
                                                 CompraCotizacionProveedor respuesta);
}
