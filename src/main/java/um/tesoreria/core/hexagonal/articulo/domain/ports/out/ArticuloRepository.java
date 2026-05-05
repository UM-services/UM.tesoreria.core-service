package um.tesoreria.core.hexagonal.articulo.domain.ports.out;

import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import java.util.List;
import java.util.Optional;
import um.tesoreria.core.hexagonal.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.model.PaginatedResponse;


public interface ArticuloRepository {
    Articulo create(Articulo articulo);
    Optional<Articulo> findById(Long id);
    List<Articulo> findAll();
    PaginatedResponse<Articulo> findAllPaginatedByTipo(String tipo, int page, int size);
    List<ArticuloSearch> findAllByStrings(List<String> conditions);
    Optional<Articulo> update(Long id, Articulo articulo);
    boolean deleteById(Long id);
    Optional<Articulo> findLast();
}
