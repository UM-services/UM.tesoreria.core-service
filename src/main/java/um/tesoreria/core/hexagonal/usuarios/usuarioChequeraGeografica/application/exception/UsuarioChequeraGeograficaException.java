package um.tesoreria.core.hexagonal.usuarios.usuarioChequeraGeografica.application.exception;

public class UsuarioChequeraGeograficaException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioChequeraGeograficaException() {
        super("UsuarioChequeraGeografica not found");
    }

    public UsuarioChequeraGeograficaException(Long usuarioChequeraGeograficaId) {
        super("Could not find UsuarioChequeraGeografica with id: " + usuarioChequeraGeograficaId);
    }

    public UsuarioChequeraGeograficaException(String message) {
        super(message);
    }

}
