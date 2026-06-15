package um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in;

import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;

public interface CreateReservaVacanteUseCase {
    ReservaVacante createReservaVacante(ReservaVacanteRequest reservaVacante);
}
