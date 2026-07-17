package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

import java.time.OffsetDateTime;

public interface FindLastByFechaUseCase {
    CuentaMovimiento findLastByFecha(OffsetDateTime fechaContable);
}
