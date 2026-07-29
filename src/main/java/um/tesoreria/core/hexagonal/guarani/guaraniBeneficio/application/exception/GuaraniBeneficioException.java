package um.tesoreria.core.hexagonal.guarani.guaraniBeneficio.application.exception;

import java.text.MessageFormat;

public class GuaraniBeneficioException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public GuaraniBeneficioException(Integer requisito) {
        super(MessageFormat.format("Cannot find GuaraniBeneficio with requisito: {0}", requisito));
    }

    public GuaraniBeneficioException(String message) {
        super(message);
    }

    public GuaraniBeneficioException() {
        super("Cannot find GuaraniBeneficio");
    }
}
