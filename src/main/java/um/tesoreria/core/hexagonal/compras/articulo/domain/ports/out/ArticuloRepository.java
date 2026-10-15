package um.tesoreria.core.hexagonal.compras.articulo.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
import java.util.List;
import java.util.Optional;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.model.PaginatedResponse;


public interface ArticuloRepository {
    /** Inserta; nunca sobrescribe un artículo existente. */
    Articulo create(Articulo articulo);
    Optional<Articulo> findById(Long id);
    /** Lee y bloquea la fila hasta el fin de la transacción; si no existe no bloquea nada. */
    Optional<Articulo> findByIdForUpdate(Long id);
    List<Articulo> findAll();
    PaginatedResponse<Articulo> findAllPaginatedByTipo(String tipo, int page, int size);
    List<ArticuloSearch> findAllByStrings(List<String> conditions);
    /** Guarda los campos de negocio sobre la fila existente {@code articulo.articuloId}. */
    Articulo update(Articulo articulo);
    void deleteById(Long id);
    Optional<Articulo> findLast();
}
