package um.tesoreria.core.hexagonal.compras.articulo.domain.ports.in;
public interface DeleteArticuloUseCase {
    boolean deleteArticulo(Long id);
}
