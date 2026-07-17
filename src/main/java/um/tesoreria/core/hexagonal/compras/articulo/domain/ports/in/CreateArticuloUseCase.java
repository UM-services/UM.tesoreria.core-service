package um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
public interface CreateArticuloUseCase {
    Articulo createArticulo(Articulo articulo);
}
