package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import java.util.Optional;

public interface UpdateProveedorUseCase {

    Optional<Proveedor> updateProveedor(Integer proveedorId, Proveedor proveedor);

}
