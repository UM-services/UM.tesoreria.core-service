package um.tesoreria.core.hexagonal.chequera.producto.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;

import java.util.Optional;

public interface GetProductoByIdUseCase {
    Optional<Producto> getProductoById(Integer productoId);
}
