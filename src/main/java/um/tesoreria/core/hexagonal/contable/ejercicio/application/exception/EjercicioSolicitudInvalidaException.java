package um.tesoreria.core.hexagonal.contable.ejercicio.application.exception;

public class EjercicioSolicitudInvalidaException extends RuntimeException {
    public EjercicioSolicitudInvalidaException(String mensaje) {
        super(mensaje);
    }
}
