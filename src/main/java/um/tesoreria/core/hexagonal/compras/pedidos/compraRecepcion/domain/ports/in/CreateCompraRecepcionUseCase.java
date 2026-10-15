package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;

public interface CreateCompraRecepcionUseCase {

    CompraRecepcion crear(CompraRecepcion recepcion);

}
