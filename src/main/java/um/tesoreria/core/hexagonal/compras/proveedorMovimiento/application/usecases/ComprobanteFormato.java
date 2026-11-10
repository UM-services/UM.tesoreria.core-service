package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.usecases;

import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimiento;

import java.math.BigDecimal;

/**
 * Formato y clasificación compartidos por los casos de uso de consulta del slice.
 * El número de comprobante replica `modConv.formatNumComprobante` del legacy
 * (`0000-00000000`).
 */
final class ComprobanteFormato {

    private ComprobanteFormato() {
    }

    static String numero(ProveedorMovimiento movimiento) {
        int prefijo = movimiento.getPrefijo() != null ? movimiento.getPrefijo() : 0;
        long numero = movimiento.getNumeroComprobante() != null ? movimiento.getNumeroComprobante() : 0L;
        return String.format("%04d-%08d", prefijo, numero);
    }

    static String descripcion(ProveedorMovimiento movimiento) {
        return movimiento.getComprobante() != null ? movimiento.getComprobante().getDescripcion() : "";
    }

    static Byte ordenPago(ProveedorMovimiento movimiento) {
        return movimiento.getComprobante() != null ? movimiento.getComprobante().getOrdenPago() : null;
    }

    static boolean esOrdenPago(ProveedorMovimiento movimiento) {
        Byte ordenPago = ordenPago(movimiento);
        return ordenPago != null && ordenPago != 0;
    }

    static BigDecimal valor(BigDecimal importe) {
        return importe != null ? importe : BigDecimal.ZERO;
    }
}
