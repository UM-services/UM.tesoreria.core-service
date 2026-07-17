package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.CreateCuentaMovimientoUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

@Component
@RequiredArgsConstructor
public class CreateCuentaMovimientoUseCaseImpl implements CreateCuentaMovimientoUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public CuentaMovimiento createCuentaMovimiento(CuentaMovimiento cuentaMovimiento) {
        return cuentaMovimientoRepository.save(cuentaMovimiento);
    }
}
