package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.CompraImpresion;

public interface GetCompraImpresionUseCase {

    /**
     * @throws um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException si no existe
     * @throws um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteEsOrdenPagoException si es una orden de pago
     */
    CompraImpresion getCompraImpresion(Long proveedorMovimientoId);
}