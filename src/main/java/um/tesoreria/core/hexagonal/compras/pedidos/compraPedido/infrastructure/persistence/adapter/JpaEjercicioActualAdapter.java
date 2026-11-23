package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.EjercicioActual;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.ports.out.EjercicioActualPort;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.Ejercicio;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.in.GetEjercicioByFechaUseCase;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Resuelve el ejercicio contable vigente reutilizando el slice de Ejercicio
 * ({@link GetEjercicioByFechaUseCase}: rango {@code fechaInicio <= fecha <= fechaFinal}, el más
 * reciente primero). El año sale de {@code fechaInicio}, igual que el {@code YEAR()} anterior.
 */
@Component
@RequiredArgsConstructor
public class JpaEjercicioActualAdapter implements EjercicioActualPort {

    private final GetEjercicioByFechaUseCase getEjercicioByFechaUseCase;

    @Override
    public Optional<EjercicioActual> findEjercicioActual(LocalDateTime fecha) {
        if (fecha == null) {
            return Optional.empty();
        }
        return getEjercicioByFechaUseCase.getEjercicioByFecha(fecha.toLocalDate())
                .map(JpaEjercicioActualAdapter::ejercicioActual);
    }

    private static EjercicioActual ejercicioActual(Ejercicio ejercicio) {
        Integer anio = ejercicio.getFechaInicio() == null ? null : ejercicio.getFechaInicio().getYear();
        return new EjercicioActual(ejercicio.getEjercicioId(), anio);
    }

}
