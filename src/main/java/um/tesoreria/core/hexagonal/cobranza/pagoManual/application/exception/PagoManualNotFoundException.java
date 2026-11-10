package um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception;

public class PagoManualNotFoundException extends RuntimeException {

    public PagoManualNotFoundException(String message) {
        super(message);
    }

}
