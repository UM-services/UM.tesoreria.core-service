package um.tesoreria.core.hexagonal.articulo.domain.ports.in;
import java.util.List;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
public interface GetAllArticulosUseCase {
    List<Articulo> getAllArticulos();
}
