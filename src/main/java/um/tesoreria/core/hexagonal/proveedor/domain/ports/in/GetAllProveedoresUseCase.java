package um.tesoreria.core.hexagonal.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import java.util.List;

public interface GetAllProveedoresUseCase {

    List<Proveedor> getAllProveedores();

}
