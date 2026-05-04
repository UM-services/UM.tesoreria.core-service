package um.tesoreria.core.hexagonal.articulo.domain.ports.out;

import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import java.util.List;
import java.util.Optional;

public interface ArticuloRepository {
    Articulo create(Articulo articulo);
    Optional<Articulo> findById(Long id);
    List<Articulo> findAll();
    Optional<Articulo> update(Long id, Articulo articulo);
    boolean deleteById(Long id);
    Optional<Articulo> findLast();
}
