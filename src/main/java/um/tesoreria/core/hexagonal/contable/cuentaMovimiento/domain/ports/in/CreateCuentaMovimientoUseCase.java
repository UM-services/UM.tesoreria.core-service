package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

public interface CreateCuentaMovimientoUseCase {
    CuentaMovimiento createCuentaMovimiento(CuentaMovimiento cuentaMovimiento);
}
