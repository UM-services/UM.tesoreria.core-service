package um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;

import java.util.UUID;

public interface FindReservaVacanteUseCase {
    ReservaVacante findByReservaVacanteId(UUID reservaVacanteId);
}
