package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion;

public interface GetOrdenPagoImpresionUseCase {

    /**
     * @throws um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException si no existe
     * @throws um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteNoImprimibleException si no es una orden de pago
     */
    OrdenPagoImpresion getOrdenPagoImpresion(Long proveedorMovimientoId);
}