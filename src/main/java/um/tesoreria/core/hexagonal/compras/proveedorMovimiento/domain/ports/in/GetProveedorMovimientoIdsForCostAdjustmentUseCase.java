package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import java.util.List;

public interface GetProveedorMovimientoIdsForCostAdjustmentUseCase {
    List<Long> getProveedorMovimientoIdsForCostAdjustment();
}
