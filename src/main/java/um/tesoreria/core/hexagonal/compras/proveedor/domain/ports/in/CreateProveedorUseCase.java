package um.tesoreria.core.hexagonal.compras.proveedor.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedor.domain.model.Proveedor;

public interface CreateProveedorUseCase {
    Proveedor createProveedor(Proveedor proveedor);
}
