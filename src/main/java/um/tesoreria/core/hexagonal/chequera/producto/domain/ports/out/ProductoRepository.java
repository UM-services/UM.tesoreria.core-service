package um.tesoreria.core.hexagonal.chequera.producto.domain.ports.out;

import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {
    List<Producto> findAll();
    Optional<Producto> findById(Integer productoId);
}
