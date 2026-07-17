package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindByCuentaMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FindByCuentaMovimientoIdUseCaseImpl implements FindByCuentaMovimientoIdUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public Optional<CuentaMovimiento> findByCuentaMovimientoId(Long cuentaMovimientoId) {
        return cuentaMovimientoRepository.findByCuentaMovimientoId(cuentaMovimientoId);
    }
}
