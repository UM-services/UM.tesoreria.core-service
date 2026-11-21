package um.tesoreria.core.hexagonal.contable.ejercicio.application.exception;

import java.time.LocalDate;

public class EjercicioNoEncontradoException extends RuntimeException {
    public EjercicioNoEncontradoException(Integer ejercicioId) {
        super("Ejercicio " + ejercicioId + " no encontrado");
    }

    public EjercicioNoEncontradoException(LocalDate fecha) {
        super("No hay ejercicio para la fecha " + fecha);
    }
}
