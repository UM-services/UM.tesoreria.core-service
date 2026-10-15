package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.application.exception;

public class PermisoEfectivoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public PermisoEfectivoException() {
        super("PermisoEfectivo not found");
    }

    public PermisoEfectivoException(String message) {
        super(message);
    }
}
