package um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out;

import java.time.LocalDate;

public interface EjercicioUsoPort {
    /** Hay movimientos contables o asientos con fecha dentro del período (ambos extremos incluidos). */
    boolean hayMovimientos(LocalDate desde, LocalDate hasta);
}
