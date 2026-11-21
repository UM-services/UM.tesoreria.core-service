package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.out;

import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;

import java.util.Optional;

public interface ValorRepository {

    Optional<Valor> findById(Integer valorId);
}
