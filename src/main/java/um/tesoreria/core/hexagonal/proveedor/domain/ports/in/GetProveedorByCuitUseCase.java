package um.tesoreria.core.hexagonal.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;
import java.util.Optional;

public interface GetProveedorByCuitUseCase {
    Optional<Proveedor> getProveedorByCuit(String cuit);
}
