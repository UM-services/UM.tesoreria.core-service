package um.tesoreria.core.hexagonal.usuarios.rol.application.exception;

public class RolException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public RolException() {
        super("Rol not found");
    }

    public RolException(Long rolId) {
        super("Could not find Rol with id: " + rolId);
    }

    public RolException(String message) {
        super(message);
    }
}
