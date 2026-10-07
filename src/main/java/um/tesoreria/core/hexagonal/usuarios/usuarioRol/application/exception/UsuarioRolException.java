package um.tesoreria.core.hexagonal.usuarios.usuarioRol.application.exception;

public class UsuarioRolException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioRolException() {
        super("UsuarioRol not found");
    }

    public UsuarioRolException(String message) {
        super(message);
    }
}
