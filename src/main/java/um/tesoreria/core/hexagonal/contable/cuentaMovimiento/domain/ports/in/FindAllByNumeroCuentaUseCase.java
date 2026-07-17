package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

import java.math.BigDecimal;
import java.util.List;

public interface FindAllByNumeroCuentaUseCase {
    List<CuentaMovimiento> findAllByNumeroCuenta(BigDecimal numeroCuenta, Boolean onlyOne);
}
