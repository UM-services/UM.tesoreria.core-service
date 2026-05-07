package um.tesoreria.core.hexagonal.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.proveedor.domain.model.Proveedor;

public interface CreateProveedorUseCase {
    Proveedor createProveedor(Proveedor proveedor);
}
