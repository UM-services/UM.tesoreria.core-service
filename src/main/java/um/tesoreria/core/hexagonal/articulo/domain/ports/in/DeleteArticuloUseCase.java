package um.tesoreria.core.hexagonal.articulo.domain.ports.in;
public interface DeleteArticuloUseCase {
    boolean deleteArticulo(Long id);
}
