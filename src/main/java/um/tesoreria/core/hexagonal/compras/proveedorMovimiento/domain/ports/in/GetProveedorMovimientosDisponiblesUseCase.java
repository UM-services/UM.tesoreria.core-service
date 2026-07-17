package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

import java.time.OffsetDateTime;
import java.util.List;

public interface GetProveedorMovimientosDisponiblesUseCase {
    List<ProveedorMovimiento> getProveedorMovimientosDisponibles(OffsetDateTime fechaDesde, OffsetDateTime fechaHasta);
}
