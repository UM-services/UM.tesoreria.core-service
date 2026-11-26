package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.LineaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindLineasByAsientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FindLineasByAsientoUseCaseImpl implements FindLineasByAsientoUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public List<LineaMovimiento> findLineasByAsiento(OffsetDateTime desde, OffsetDateTime hasta, Integer ordenContable) {
        return cuentaMovimientoRepository.findLineasByAsiento(desde, hasta, ordenContable);
    }
}
