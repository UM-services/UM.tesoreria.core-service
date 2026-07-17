package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.DeleteAllByCuentaMovimientoIdInUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DeleteAllByCuentaMovimientoIdInUseCaseImpl implements DeleteAllByCuentaMovimientoIdInUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public void deleteAllByCuentaMovimientoIdIn(List<Long> cuentaMovimientoIds) {
        cuentaMovimientoRepository.deleteAllByCuentaMovimientoIdIn(cuentaMovimientoIds);
    }
}
