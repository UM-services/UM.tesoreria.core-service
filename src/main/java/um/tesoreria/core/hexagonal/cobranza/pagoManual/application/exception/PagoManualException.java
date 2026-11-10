package um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception;

public class PagoManualException extends RuntimeException {

    private final boolean conflict;

    public PagoManualException(String message) {
        this(message, false);
    }

    public PagoManualException(String message, boolean conflict) {
        super(message);
        this.conflict = conflict;
    }

    public boolean isConflict() {
        return conflict;
    }

}
