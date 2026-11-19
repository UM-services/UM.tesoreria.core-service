package um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.contable.cuentaMovimiento.domain.model.SumaCuenta;

import java.time.OffsetDateTime;
import java.util.List;

public interface SumarPorCuentaUseCase {
    List<SumaCuenta> sumarPorCuenta(OffsetDateTime desde, OffsetDateTime hasta, Byte apertura);
}