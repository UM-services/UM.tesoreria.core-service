package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.CreateReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.FindReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.UpdateReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.web.dto.ReservaVacanteRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservaVacanteService {

    private final CreateReservaVacanteUseCase createReservaVacanteUseCase;
    private final FindReservaVacanteUseCase findReservaVacanteUseCase;
    private final UpdateReservaVacanteUseCase updateReservaVacanteUseCase;

    public ReservaVacante createReservaVacante(ReservaVacanteRequest reservaVacanteRequest) {
        return createReservaVacanteUseCase.createReservaVacante(reservaVacanteRequest);
    }

    public ReservaVacante findReservaVacante(UUID reservaVacanteId) {
        return findReservaVacanteUseCase.findByReservaVacanteId(reservaVacanteId);
    }

    public ReservaVacante updateReservaVacante(ReservaVacante newReservaVacante, UUID reservaVacanteId) {
        return updateReservaVacanteUseCase.update(newReservaVacante, reservaVacanteId);
    }
}
