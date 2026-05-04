package um.tesoreria.core.hexagonal.articulo.domain.ports.in;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
public interface CreateArticuloUseCase {
    Articulo createArticulo(Articulo articulo);
}
