package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.ports.out;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion;

import java.util.List;

public interface TrazaContableOrdenPagoRepository {

    /**
     * Traza contable consolidada de una orden de pago, igual que
     * {@code vw_trazaContablePagosConsolidada} del legacy: importe neto por cuenta,
     * en el debe si es positivo y en el haber si es negativo, sin cuentas en cero y
     * con el debe primero.
     */
    List<OrdenPagoImpresion.TrazaLinea> findConsolidadaByOrdenPagoId(Long proveedorMovimientoId);
}