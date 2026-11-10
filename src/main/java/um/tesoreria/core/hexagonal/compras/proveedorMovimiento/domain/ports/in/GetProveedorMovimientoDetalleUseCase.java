package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoDetalle;

public interface GetProveedorMovimientoDetalleUseCase {

    ProveedorMovimientoDetalle getProveedorMovimientoDetalle(Long proveedorMovimientoId);
}
