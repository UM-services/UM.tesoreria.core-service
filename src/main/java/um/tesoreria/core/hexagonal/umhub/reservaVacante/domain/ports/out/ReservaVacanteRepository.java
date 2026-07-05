package um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out;

import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;

import java.util.Optional;
import java.util.UUID;

public interface ReservaVacanteRepository {
    ReservaVacante create(ReservaVacante reservaVacante);
    Optional<ReservaVacante> findByReservaVacanteId(UUID reservaVacanteId);
    ReservaVacante update(ReservaVacante reservaVacante, UUID reservaVacanteId);
}
