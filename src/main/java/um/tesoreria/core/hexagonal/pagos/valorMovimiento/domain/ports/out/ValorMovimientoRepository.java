package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out;

import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.ValorMovimiento;

import java.util.List;
import java.util.Optional;

public interface ValorMovimientoRepository {

    Optional<ValorMovimiento> findById(Long valorMovimientoId);

    List<ValorMovimiento> findAllByIdIn(List<Long> valorMovimientoIds);

    ValorMovimiento save(ValorMovimiento valorMovimiento);
}
