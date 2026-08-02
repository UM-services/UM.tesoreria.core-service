package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.application.exception;

import java.text.MessageFormat;

public class GuaraniUbicacionException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public GuaraniUbicacionException(Integer id) {
        super(MessageFormat.format("Cannot find GuaraniUbicacion with id: {0}", id));
    }
}
