package um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;

import java.util.List;

public interface GetProveedorPagosByProveedorMovimientoIdPagoUseCase {

    List<ProveedorPago> getProveedorPagos(Long proveedorMovimientoIdPago);
}
