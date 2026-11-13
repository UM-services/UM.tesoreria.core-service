package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception;

/** La impresión de compras no aplica a una orden de pago (esa se pide en {@code /impresion}). */
public class ComprobanteEsOrdenPagoException extends RuntimeException {

    private static final long serialVersionUID = 3197740421160081763L;

    public ComprobanteEsOrdenPagoException(Long proveedorMovimientoId) {
        super("El comprobante " + proveedorMovimientoId + " es una orden de pago");
    }
}