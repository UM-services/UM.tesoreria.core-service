package um.tesoreria.core.hexagonal.contable.ejercicio.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.model.EjercicioFechas;
import um.tesoreria.core.hexagonal.contable.ejercicio.domain.ports.out.EjercicioUsoPort;
import um.tesoreria.core.hexagonal.contable.asiento.domain.ports.in.ExistsAsientoByFechaBetweenUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.ExistsByFechaContableBetweenUseCase;

import java.time.LocalDate;
import java.time.OffsetDateTime;

/**
 * movcon y asiento se vinculan por fecha, sin FK, y hay asientos sin movimientos y viceversa:
 * hacen falta las dos consultas. Cruce con los slices de movimientos y asientos por sus puertos in públicos.
 */
@Component
@RequiredArgsConstructor
public class EjercicioUsoAdapter implements EjercicioUsoPort {

    private final ExistsByFechaContableBetweenUseCase movimientos;
    private final ExistsAsientoByFechaBetweenUseCase asientos;

    @Override
    public boolean hayMovimientos(LocalDate desde, LocalDate hasta) {
        OffsetDateTime inicio = EjercicioFechas.aInicioDeDia(desde);
        OffsetDateTime fin = EjercicioFechas.aFinDeDia(hasta);
        return movimientos.existsByFechaContableBetween(inicio, fin) || asientos.existsByFechaBetween(inicio, fin);
    }
}
