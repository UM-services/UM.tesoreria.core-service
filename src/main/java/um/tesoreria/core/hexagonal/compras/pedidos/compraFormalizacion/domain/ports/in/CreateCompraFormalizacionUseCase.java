package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;

public interface CreateCompraFormalizacionUseCase {

    CompraFormalizacion crear(CompraFormalizacion formalizacion);

}
