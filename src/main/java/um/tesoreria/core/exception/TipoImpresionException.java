package um.tesoreria.core.exception;

public class TipoImpresionException extends RuntimeException {

    private static final long serialVersionUID = -8171960148902614521L;

    public TipoImpresionException(Integer tipoImpresionId) {
        super("Cannot find TipoImpresion " + tipoImpresionId);
    }

}