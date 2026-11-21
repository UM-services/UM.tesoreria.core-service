package um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompra;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraDetalle;
import um.tesoreria.core.hexagonal.pagos.facturaCompra.domain.model.FacturaCompraResultado;

public interface FacturaCompraUseCase {

    FacturaCompraResultado registrar(FacturaCompra factura);

    FacturaCompraDetalle consultar(Long proveedorMovimientoId);

    boolean existe(Integer proveedorId, Integer comprobanteId, Integer prefijo, Long numeroComprobante);

}
