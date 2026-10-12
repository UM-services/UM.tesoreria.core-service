package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;

public interface FormalizarCompraCotizacionUseCase {

    CompraCotizacion formalizar(Long compraCotizacionId);

}
