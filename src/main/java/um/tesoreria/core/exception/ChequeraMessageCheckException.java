package um.tesoreria.core.exception;

import java.util.UUID;

public class ChequeraMessageCheckException extends RuntimeException {

    public ChequeraMessageCheckException(UUID chequeraMessageCheckId) {
        super("No se encontró el chequeraMessageCheckId: " + chequeraMessageCheckId);
    }

}
