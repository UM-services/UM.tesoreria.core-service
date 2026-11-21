package um.tesoreria.core.hexagonal.contable.asiento.application.exception;

/** No se pudo reservar un orden contable luego de agotar los reintentos por concurrencia. */
public class AsientoSecuenciaException extends RuntimeException {

    public AsientoSecuenciaException(String message, Throwable cause) {
        super(message, cause);
    }

}
