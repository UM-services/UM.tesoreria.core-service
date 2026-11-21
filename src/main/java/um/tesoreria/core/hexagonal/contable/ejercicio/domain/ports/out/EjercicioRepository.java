package um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out;

import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;

import java.time.LocalDate;
import java.util.Optional;

public interface EjercicioRepository {
    /** Lee el último ejercicio con bloqueo de escritura; tiene que ser la primera lectura de esa fila. */
    Optional<Ejercicio> findLastWithLock();

    /** Lee el ejercicio con bloqueo de escritura; tiene que ser la primera lectura de esa fila. */
    Optional<Ejercicio> findWithLockByEjercicioId(Integer ejercicioId);

    Optional<Ejercicio> findByEjercicioId(Integer ejercicioId);

    /** Ejercicio que contiene la fecha (fechaInicio <= fecha <= fechaFinal). */
    Optional<Ejercicio> findByFecha(LocalDate fecha);

    boolean existsByEjercicioId(Integer ejercicioId);

    /** INSERT estricto: nunca actualiza una fila existente. */
    Ejercicio create(Ejercicio ejercicio);

    Ejercicio update(Ejercicio ejercicio);

    void deleteByEjercicioId(Integer ejercicioId);
}
