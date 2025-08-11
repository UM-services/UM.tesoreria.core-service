package um.tesoreria.core.exception;

public class MercadoPagoContextException extends RuntimeException {
    public MercadoPagoContextException(String message,  Long id) {
        super(message + " " + id);
    }

    public MercadoPagoContextException(String idMercadoPago) {
        super("Cannot find MercadoPagoContext with idMercadoPago: " + idMercadoPago);
    }

}
