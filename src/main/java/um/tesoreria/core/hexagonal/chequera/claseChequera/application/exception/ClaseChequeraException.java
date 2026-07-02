package um.tesoreria.core.hexagonal.chequera.claseChequera.application.exception;

public class ClaseChequeraException extends RuntimeException {

    private static final long serialVersionUID = -765432109876543210L;

    public ClaseChequeraException() {
        super("Cannot find ClaseChequera");
    }

}
