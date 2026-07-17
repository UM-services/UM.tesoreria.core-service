package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

public interface DeleteProveedorMovimientoUseCase {
    void deleteProveedorMovimiento(Long proveedorMovimientoId);

    void deleteProveedorMovimiento(ProveedorMovimiento proveedorMovimiento);
}
