package um.tesoreria.core.hexagonal.mercadoPagoContext.application.exception;

public class MercadoPagoContextException extends RuntimeException {

    public MercadoPagoContextException(String message, Long id) {
        super(message + " " + id);
    }

}
