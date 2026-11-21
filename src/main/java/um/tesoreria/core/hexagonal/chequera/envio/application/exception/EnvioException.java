package um.tesoreria.core.hexagonal.chequera.envio.application.exception;

import lombok.Getter;

/** Error de negocio del envío masivo; el {@code codigo} es parte del contrato REST. */
@Getter
public class EnvioException extends RuntimeException {

    private final String codigo;

    public EnvioException(String codigo, String mensaje) {
        super(mensaje);
        this.codigo = codigo;
    }
}
