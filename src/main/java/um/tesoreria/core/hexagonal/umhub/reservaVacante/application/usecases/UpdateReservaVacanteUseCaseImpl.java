package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.application.exception.ReservaVacanteException;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.UpdateReservaVacanteUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out.ReservaVacanteRepository;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UpdateReservaVacanteUseCaseImpl implements UpdateReservaVacanteUseCase {

    private final ReservaVacanteRepository repository;

    @Override
    public ReservaVacante update(ReservaVacante newReservaVacante, UUID reservaVacanteId) {
        if (reservaVacanteId == null || newReservaVacante == null) {
            throw new IllegalArgumentException("Invalid input parameters");
        }
        log.debug("\n\nUpdating ReservaVacante with id: {}\n\n", reservaVacanteId);

        return repository.findByReservaVacanteId(reservaVacanteId)
                .map(existing -> {
                    existing.setEstado(newReservaVacante.getEstado());
                    existing.setImporte(newReservaVacante.getImporte());
                    existing.setVencimiento(newReservaVacante.getVencimiento());
                    return repository.update(existing, reservaVacanteId);
                })
                .orElseThrow(() -> new ReservaVacanteException(reservaVacanteId));
    }
}
