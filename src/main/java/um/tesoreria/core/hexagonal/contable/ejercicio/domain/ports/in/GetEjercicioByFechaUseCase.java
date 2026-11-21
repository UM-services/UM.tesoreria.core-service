package um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

import java.time.LocalDate;
import java.util.Optional;

public interface GetEjercicioByFechaUseCase {

    Optional<Ejercicio> getEjercicioByFecha(LocalDate fecha);
}
