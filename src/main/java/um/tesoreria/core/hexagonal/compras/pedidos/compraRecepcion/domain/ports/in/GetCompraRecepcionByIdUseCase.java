package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;

import java.util.Optional;

public interface GetCompraRecepcionByIdUseCase {

    Optional<CompraRecepcion> getById(Long compraRecepcionId);

}
