package um.tesoreria.core.hexagonal.contable.ejercicio.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.GetEjercicioByFechaUseCase;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioRepository;

import java.time.LocalDate;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class GetEjercicioByFechaUseCaseImpl implements GetEjercicioByFechaUseCase {

    private final EjercicioRepository ejercicioRepository;

    @Override
    public Optional<Ejercicio> getEjercicioByFecha(LocalDate fecha) {
        if (fecha == null) {
            return Optional.empty();
        }
        return ejercicioRepository.findByFecha(fecha);
    }
}
