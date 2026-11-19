package um.tesoreria.core.hexagonal.usuarios.rol.application.exception;

/**
 * El manifiesto `roles.yaml` no se pudo leer, no cumple el contrato o referencia
 * permisos inexistentes.
 */
public class RolManifestException extends RuntimeException {

    public RolManifestException(String message) {
        super(message);
    }

    public RolManifestException(String message, Throwable cause) {
        super(message, cause);
    }
}
