package um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in;
import java.util.Optional;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
public interface UpdateArticuloUseCase {
    Optional<Articulo> updateArticulo(Long id, Articulo articulo);
}
