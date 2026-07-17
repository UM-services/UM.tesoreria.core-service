package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.ProveedorSearch;
import java.util.List;

public interface SearchProveedoresUseCase {
    List<ProveedorSearch> searchProveedores(List<String> conditions);
}
