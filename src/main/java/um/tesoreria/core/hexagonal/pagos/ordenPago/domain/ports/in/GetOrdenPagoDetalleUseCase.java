package um.tesoreria.core.hexagonal.pagos.ordenPago.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.ordenPago.domain.model.OrdenPagoDetalle;

public interface GetOrdenPagoDetalleUseCase {

    OrdenPagoDetalle getDetalle(Long proveedorMovimientoId);
}
