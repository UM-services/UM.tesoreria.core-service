package um.tesoreria.core.exception.facade;

import java.text.MessageFormat;
import java.time.OffsetDateTime;

public class ContableException extends RuntimeException {

    public ContableException(OffsetDateTime fechaContable, Integer ordenContable) {
        super(MessageFormat.format("Error with Asiento -> {0}/{1}", fechaContable, ordenContable));
    }

    public ContableException(String message) {
        super(message);
    }
}
