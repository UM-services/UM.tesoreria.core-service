package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

public interface CreateProveedorMovimientoUseCase {
    ProveedorMovimiento createProveedorMovimiento(ProveedorMovimiento proveedorMovimiento);
}
