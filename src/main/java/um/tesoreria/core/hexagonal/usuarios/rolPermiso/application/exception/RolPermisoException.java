package um.tesoreria.core.hexagonal.usuarios.rolPermiso.application.exception;

public class RolPermisoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RolPermisoException() {
        super("RolPermiso not found");
    }

    public RolPermisoException(String message) {
        super(message);
    }
}
