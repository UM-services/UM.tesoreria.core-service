package um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;

public interface CreateProveedorPagoUseCase {

    ProveedorPago createProveedorPago(ProveedorPago proveedorPago);
}
