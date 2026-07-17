package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

import java.util.Optional;

public interface GetProveedorMovimientoByIdUseCase {
    Optional<ProveedorMovimiento> getProveedorMovimientoById(Long proveedorMovimientoId);
}
