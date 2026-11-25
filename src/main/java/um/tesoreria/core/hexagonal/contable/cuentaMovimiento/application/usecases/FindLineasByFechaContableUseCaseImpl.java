package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindLineasByFechaContableUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindLineasByFechaContableUseCaseImpl implements FindLineasByFechaContableUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public List<LineaMovimiento> findLineasByFechaContable(OffsetDateTime desde, OffsetDateTime hasta, int limite) {
        return cuentaMovimientoRepository.findLineasByFechaContable(desde, hasta, limite);
    }
}
