package um.tesoreria.core.hexagonal.chequera.producto.domain.ports.in;

import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;

import java.util.List;

public interface GetAllProductosUseCase {
    List<Producto> getAllProductos();
}
