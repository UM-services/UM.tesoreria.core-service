package um.tesoreria.core.exception;

public class MercadoPagoContextException extends RuntimeException {
    public MercadoPagoContextException(String message,  Long id) {
        super(message + " " + id);
    }
}
