package um.tesoreria.core.exception;

public class DebitoTipoException extends RuntimeException {

    private static final long serialVersionUID = -2841097430571158364L;

    public DebitoTipoException(Integer debitoTipoId) {
        super("Cannot find DebitoTipo " + debitoTipoId);
    }

}