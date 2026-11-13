package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.application.usecases;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in.FindAllByProveedorMovimientoIdUseCase;
import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.out.CuentaMovimientoRepository;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FindAllByProveedorMovimientoIdUseCaseImpl implements FindAllByProveedorMovimientoIdUseCase {

    private final CuentaMovimientoRepository cuentaMovimientoRepository;

    @Override
    public List<CuentaMovimiento> findAllByProveedorMovimientoId(Long proveedorMovimientoId) {
        return cuentaMovimientoRepository.findAllByProveedorMovimientoId(proveedorMovimientoId);
    }
}