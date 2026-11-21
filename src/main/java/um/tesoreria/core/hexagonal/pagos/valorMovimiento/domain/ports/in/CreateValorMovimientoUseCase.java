package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

public interface CreateValorMovimientoUseCase {

    ValorMovimiento createValorMovimiento(ValorMovimiento valorMovimiento);
}
