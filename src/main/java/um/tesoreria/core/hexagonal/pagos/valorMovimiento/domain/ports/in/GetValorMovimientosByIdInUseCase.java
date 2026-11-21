package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

import java.util.List;

public interface GetValorMovimientosByIdInUseCase {

    List<ValorMovimiento> getValorMovimientos(List<Long> valorMovimientoIds);
}
