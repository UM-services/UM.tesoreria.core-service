package um.tesoreria.core.hexagonal.contable.ejercicio.application.exception;

public class EjercicioNoEncontradoException extends RuntimeException {
    public EjercicioNoEncontradoException(Integer ejercicioId) {
        super("Ejercicio " + ejercicioId + " no encontrado");
    }
}
