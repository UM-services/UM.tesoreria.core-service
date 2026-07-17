package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;
import java.util.Optional;

public interface GetProveedorByCuitUseCase {
    Optional<Proveedor> getProveedorByCuit(String cuit);
}
