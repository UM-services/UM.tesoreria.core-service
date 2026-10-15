package um.tesoreria.core.hexagonal.usuarios.permiso.application.exception;

public class PermisoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PermisoException() {
        super("Permiso not found");
    }

    public PermisoException(Long permisoId) {
        super("Could not find Permiso with id: " + permisoId);
    }

    public PermisoException(String message) {
        super(message);
    }
}
