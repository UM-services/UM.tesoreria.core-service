package um.tesoreria.core.hexagonal.chequera.chequeraPago.application.exception;

import java.text.MessageFormat;

public class ChequeraPagoException extends RuntimeException {

    private static final long serialVersionUID = -5586943026419608961L;

    public ChequeraPagoException() {
        super("Cannot find ChequeraPago");
    }

    public ChequeraPagoException(Long chequeraPagoId) {
        super(MessageFormat.format("Cannot find ChequeraPago {0}", chequeraPagoId));
    }

    public ChequeraPagoException(String idMercadoPago) {
        super(MessageFormat.format("Cannot find ChequeraPago with idMercadoPago {0}", idMercadoPago));
    }

}
