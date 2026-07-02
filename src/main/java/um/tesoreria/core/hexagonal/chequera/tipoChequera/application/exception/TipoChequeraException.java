package um.tesoreria.core.hexagonal.chequera.tipoChequera.application.exception;

import java.text.MessageFormat;

public class TipoChequeraException extends RuntimeException {

    private static final long serialVersionUID = -8278048406744498690L;

    public TipoChequeraException(Integer tipoChequeraId) {
        super(MessageFormat.format("Cannot find TipoChequera {0}", tipoChequeraId));
    }

    public TipoChequeraException() {
        super("Cannot find TipoChequera");
    }
}
