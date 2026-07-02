package um.tesoreria.core.hexagonal.lectivo.application.exception;

import java.text.MessageFormat;
import java.time.OffsetDateTime;

public class LectivoException extends RuntimeException {

    private static final long serialVersionUID = -8437455926582914496L;

    public LectivoException(Integer lectivoId) {
        super(MessageFormat.format("Cannot find Lectivo {0}", lectivoId));
    }

    public LectivoException(OffsetDateTime fecha) {
        super(MessageFormat.format("Cannot find Lectivo {0}", fecha));
    }

    public LectivoException() {
        super("Cannot find Lectivo");
    }
}
