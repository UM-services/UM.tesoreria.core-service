package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

import java.util.List;

public interface GetProveedorMovimientosEliminablesUseCase {
    List<ProveedorMovimiento> getProveedorMovimientosEliminables(Integer ejercicioId);
}
