package um.tesoreria.core.hexagonal.articulo.domain.ports.in;

import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.model.PaginatedResponse;

public interface GetPaginatedArticulosUseCase {
    PaginatedResponse<Articulo> getPaginatedArticulosByTipo(String tipo, int page, int size);
}
