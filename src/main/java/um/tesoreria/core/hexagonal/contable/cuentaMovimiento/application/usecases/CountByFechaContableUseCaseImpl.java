package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.CountByFechaContableUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class CountByFechaContableUseCaseImpl implements CountByFechaContableUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public long countByFechaContable(OffsetDateTime desde, OffsetDateTime hasta) {
        return cuentaMovimientoRepository.countByFechaContable(desde, hasta);
    }
}
