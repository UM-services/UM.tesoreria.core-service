package um.tesoreria.rest.exception;


import java.text.MessageFormat;

public class BancoMovimientoException extends RuntimeException {

    public BancoMovimientoException() {
        super("Cannot find BancoMovimiento");
    }

    public BancoMovimientoException(Long bancoMovimientoId) {
        super(MessageFormat.format("Cannot find BancoMovimiento with bancoMovimientoId={0}", bancoMovimientoId));
    }
}
