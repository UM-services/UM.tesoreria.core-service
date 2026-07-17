package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

import java.util.Optional;

public interface FindByCuentaMovimientoIdUseCase {
    Optional<CuentaMovimiento> findByCuentaMovimientoId(Long cuentaMovimientoId);
}
