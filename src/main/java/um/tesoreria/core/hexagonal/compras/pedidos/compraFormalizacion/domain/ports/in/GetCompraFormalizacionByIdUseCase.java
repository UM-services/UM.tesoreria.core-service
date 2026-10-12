package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;

import java.util.Optional;

public interface GetCompraFormalizacionByIdUseCase {

    Optional<CompraFormalizacion> getById(Long compraFormalizacionId);

}
