package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.exception;

public class UsuarioPermisoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public UsuarioPermisoException() {
        super("UsuarioPermiso not found");
    }

    public UsuarioPermisoException(String message) {
        super(message);
    }
}
