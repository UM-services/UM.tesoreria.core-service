package um.tesoreria.core.hexagonal.pagos.entrega.domain.ports.out;

import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;

import java.util.List;
import java.util.Optional;

public interface EntregaRepository {

    Optional<Entrega> findById(Long entregaId);

    List<Entrega> findAllByEntregaIdIn(List<Long> entregaIds);

    Entrega save(Entrega entrega);
}
