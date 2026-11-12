package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception;

/** El comprobante existe pero su impresión todavía no está soportada (no es una orden de pago). */
public class ComprobanteNoImprimibleException extends RuntimeException {

    private static final long serialVersionUID = 5718402215630193344L;

    public ComprobanteNoImprimibleException(Long proveedorMovimientoId) {
        super("El comprobante " + proveedorMovimientoId + " no es una orden de pago");
    }
}