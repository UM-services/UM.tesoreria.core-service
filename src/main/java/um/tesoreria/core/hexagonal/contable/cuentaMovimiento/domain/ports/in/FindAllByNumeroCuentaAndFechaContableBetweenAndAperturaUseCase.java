package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public interface FindAllByNumeroCuentaAndFechaContableBetweenAndAperturaUseCase {
    List<CuentaMovimiento> findAllByNumeroCuentaAndFechaContableBetweenAndApertura(BigDecimal numeroCuenta, OffsetDateTime desde, OffsetDateTime hasta, Byte apertura);
}
