package um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in;
import um.tesoreria.core.hexagonal.compras.articulo.domain.model.Articulo;
public interface UpdateArticuloUseCase {
    Articulo updateArticulo(Long id, Articulo cambios);
}
