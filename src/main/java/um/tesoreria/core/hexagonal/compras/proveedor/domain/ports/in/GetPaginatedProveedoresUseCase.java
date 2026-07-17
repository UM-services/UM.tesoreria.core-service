package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

import um.tesoreria.core.model.PaginatedResponse;
import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;

public interface GetPaginatedProveedoresUseCase {
    PaginatedResponse<Proveedor> getPaginatedProveedores(int page, int size);
}
