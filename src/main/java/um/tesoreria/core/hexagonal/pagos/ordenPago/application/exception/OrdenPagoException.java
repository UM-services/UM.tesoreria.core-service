package um.tesoreria.core.hexagonal.pagos.ordenPago.application.exception;

public class OrdenPagoException extends RuntimeException {

    public OrdenPagoException() {
        super("Orden de pago no encontrada");
    }

    public OrdenPagoException(Long proveedorMovimientoId) {
        super("No se encontró la orden de pago con id: " + proveedorMovimientoId);
    }

    public OrdenPagoException(String message) {
        super(message);
    }
}
