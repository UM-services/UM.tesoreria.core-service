package um.tesoreria.core.hexagonal.chequera.emision.application.exception;

import lombok.Getter;

/**
 * Error de negocio de la emisión. El {@code codigo} es parte del contrato REST y lo
 * consume el frontend para mostrar el mensaje correspondiente.
 */
@Getter
public class EmisionException extends RuntimeException {

    private final String codigo;

    public EmisionException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
}
