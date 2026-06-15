package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.CreateReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;

@Service
@RequiredArgsConstructor
public class ReservaVacanteService {

    private final CreateReservaVacanteUseCase createReservaVacanteUseCase;

    public ReservaVacante createReservaVacante(ReservaVacanteRequest reservaVacanteRequest) {
        return createReservaVacanteUseCase.createReservaVacante(reservaVacanteRequest);
    }
}
