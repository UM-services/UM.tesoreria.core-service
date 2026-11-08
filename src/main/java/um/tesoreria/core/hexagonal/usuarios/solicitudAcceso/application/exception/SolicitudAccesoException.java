package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.application.exception;

public class SolicitudAccesoException extends RuntimeException {

    public SolicitudAccesoException(String message) {
        super(message);
    }
}
