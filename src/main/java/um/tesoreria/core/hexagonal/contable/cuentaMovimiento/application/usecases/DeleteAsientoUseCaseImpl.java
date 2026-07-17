package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.DeleteAsientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class DeleteAsientoUseCaseImpl implements DeleteAsientoUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public void deleteAsiento(OffsetDateTime fechaContable, Integer ordenContable) {
        cuentaMovimientoRepository.deleteAllByFechaContableAndOrdenContable(fechaContable, ordenContable);
    }
}
