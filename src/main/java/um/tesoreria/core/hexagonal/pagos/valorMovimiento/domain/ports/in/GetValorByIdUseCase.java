package um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.ports.in;

import um.tesoreria.core.hexagonal.pagos.valorMovimiento.domain.model.Valor;

import java.util.Optional;

public interface GetValorByIdUseCase {

    Optional<Valor> getValorById(Integer valorId);
}
