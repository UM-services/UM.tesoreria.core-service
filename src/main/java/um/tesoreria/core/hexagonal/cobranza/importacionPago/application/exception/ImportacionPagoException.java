package um.tesoreria.core.hexagonal.cobranza.importacionPago.application.exception;

public class ImportacionPagoException extends RuntimeException {

    public ImportacionPagoException(String message) {
        super(message);
    }

    public ImportacionPagoException(String message, Throwable cause) {
        super(message, cause);
    }

}
