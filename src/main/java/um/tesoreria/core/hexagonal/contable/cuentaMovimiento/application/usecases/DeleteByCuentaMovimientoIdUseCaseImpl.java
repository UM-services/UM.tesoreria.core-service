package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.DeleteByCuentaMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

@Component
@RequiredArgsConstructor
public class DeleteByCuentaMovimientoIdUseCaseImpl implements DeleteByCuentaMovimientoIdUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public void deleteByCuentaMovimientoId(Long cuentaMovimientoId) {
        cuentaMovimientoRepository.deleteByCuentaMovimientoId(cuentaMovimientoId);
    }
}
