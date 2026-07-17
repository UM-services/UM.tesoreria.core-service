package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.CuentaMovimiento;

import java.time.OffsetDateTime;
import java.util.List;

public interface FindAllByAsientoUseCase {
    List<CuentaMovimiento> findAllByAsiento(OffsetDateTime fechaContable, Integer ordenContable, Integer itemDesde, Integer debita);
}
