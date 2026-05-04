package um.tesoreria.core.hexagonal.articulo.domain.ports.in;
import java.util.List;
import um.tesoreria.core.hexagonal.articulo.domain.model.ArticuloSearch;
public interface SearchArticulosUseCase {
    List<ArticuloSearch> searchArticulos(List<String> conditions);
}
