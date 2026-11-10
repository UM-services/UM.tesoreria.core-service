package um.tesoreria.core.hexagonal.usuarios.permiso.application.exception;

/**
 * El manifiesto `permisos.yaml` no se pudo leer o no cumple el contrato.
 */
public class PermisoManifestException extends RuntimeException {

    public PermisoManifestException(String message) {
        super(message);
    }

    public PermisoManifestException(String message, Throwable cause) {
        super(message, cause);
    }
}
