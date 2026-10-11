package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;

/** Registra la recomendación del proceso (proveedor elegido + fundamento). */
public interface RecomendarCompraCotizacionUseCase {

    CompraCotizacion recomendar(Long compraCotizacionId, Long compraCotizacionProveedorId, String fundamento);
}
