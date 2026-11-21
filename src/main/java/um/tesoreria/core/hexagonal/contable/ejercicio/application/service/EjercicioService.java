package um.tesoreria.core.hexagonal.contable.ejercicio.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.contable.ejercicio.application.exception.EjercicioNoEncontradoException;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.GetEjercicioByFechaUseCase;

import java.time.LocalDate;
import java.util.Optional;

@Service("contableEjercicioService")
@RequiredArgsConstructor
public class EjercicioService {

    private final GetEjercicioByFechaUseCase getEjercicioByFecha;

    public Optional<Ejercicio> findByFecha(LocalDate fecha) {
        return getEjercicioByFecha.getEjercicioByFecha(fecha);
    }

    /**
     * Igual que {@code ejercicioRep.findByFecha(dtpFecha)} del legacy: si no hay ejercicio no se puede operar.
     */
    public Ejercicio getByFecha(LocalDate fecha) {
        return findByFecha(fecha)
                .orElseThrow(() -> new EjercicioNoEncontradoException(fecha));
    }
}
