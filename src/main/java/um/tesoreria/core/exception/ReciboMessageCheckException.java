package um.tesoreria.core.exception;

import java.util.UUID;

public class ReciboMessageCheckException extends RuntimeException {

    public ReciboMessageCheckException(UUID reciboMessageCheckId) {
        super("No se encontró el reciboMessageCheckId: " + reciboMessageCheckId);
    }

}
