package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindLastByFechaUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.time.OffsetDateTime;

@Component
@RequiredArgsConstructor
public class FindLastByFechaUseCaseImpl implements FindLastByFechaUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public CuentaMovimiento findLastByFecha(OffsetDateTime fechaContable) {
        return cuentaMovimientoRepository.findTopByFechaContableOrderByOrdenContableDesc(fechaContable)
                .orElseGet(() -> CuentaMovimiento.builder().build());
    }
}
