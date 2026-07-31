package um.tesoreria.core.hexagonal.extern.facultad.tesoreriaEstado.application.exception;

import java.math.BigDecimal;
import java.text.MessageFormat;

public class TesoreriaEstadoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public TesoreriaEstadoException() {
        super("Cannot find TesoreriaEstado");
    }

    public TesoreriaEstadoException(Integer facultadId, BigDecimal personaId, Integer documentoId) {
        super(MessageFormat.format(
                "Cannot find TesoreriaEstado with facultadId: {0}, personaId: {1}, documentoId: {2}",
                facultadId, personaId, documentoId));
    }

    public TesoreriaEstadoException(String message) {
        super(message);
    }
}
