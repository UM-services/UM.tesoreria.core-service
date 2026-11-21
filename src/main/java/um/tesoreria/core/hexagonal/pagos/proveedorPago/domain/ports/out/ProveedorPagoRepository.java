package um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.ports.out;

import um.tesoreria.core.hexagonal.pagos.proveedorPago.domain.model.ProveedorPago;

import java.util.List;

public interface ProveedorPagoRepository {

    List<ProveedorPago> findAllByProveedorMovimientoIdPago(Long proveedorMovimientoIdPago);

    List<ProveedorPago> findAllByProveedorMovimientoIdFactura(Long proveedorMovimientoIdFactura);

    ProveedorPago save(ProveedorPago proveedorPago);
}
