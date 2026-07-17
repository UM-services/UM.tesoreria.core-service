package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import java.util.List;

public interface GetAllProveedoresUseCase {

    List<Proveedor> getAllProveedores();

}
