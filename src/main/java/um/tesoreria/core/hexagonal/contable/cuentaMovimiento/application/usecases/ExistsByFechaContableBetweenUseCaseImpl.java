package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.ExistsByFechaContableBetweenUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class ExistsByFechaContableBetweenUseCaseImpl implements ExistsByFechaContableBetweenUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public boolean existsByFechaContableBetween(OffsetDateTime desde, OffsetDateTime hasta) {
        return cuentaMovimientoRepository.existsByFechaContableBetween(desde, hasta);
    }
}
