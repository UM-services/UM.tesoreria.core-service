package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

import java.util.Optional;

public interface GetValorMovimientoByIdUseCase {

    Optional<ValorMovimiento> getValorMovimientoById(Long valorMovimientoId);
}
