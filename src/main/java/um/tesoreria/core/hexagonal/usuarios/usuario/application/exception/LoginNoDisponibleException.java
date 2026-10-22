package um.tesoreria.core.hexagonal.usuarios.usuario.application.exception;

/**
 * No se pudo asignar un login único al usuario (colisión persistente o login
 * explícito ya utilizado).
 */
public class LoginNoDisponibleException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public LoginNoDisponibleException(String message) {
        super(message);
    }
}
