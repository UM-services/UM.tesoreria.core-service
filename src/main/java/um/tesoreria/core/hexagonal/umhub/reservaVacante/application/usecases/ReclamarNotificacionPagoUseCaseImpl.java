package um.tesoreria.core.hexagonal.umhub.reservaVacante.application.usecases;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.application.exception.ReservaVacanteException;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.in.ReclamarNotificacionPagoUseCase;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out.ReservaVacanteRepository;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReclamarNotificacionPagoUseCaseImpl implements ReclamarNotificacionPagoUseCase {

    private final ReservaVacanteRepository reservaVacanteRepository;

    @Override
    @Transactional
    public boolean reclamar(UUID reservaVacanteId) {
        ReservaVacante reservaVacante = reservaVacanteRepository.findWithLockByReservaVacanteId(reservaVacanteId)
                .orElseThrow(() -> new ReservaVacanteException(reservaVacanteId));

        if (Boolean.TRUE.equals(reservaVacante.getNotificacionPago())) {
            log.info("Notificación de pago ya reclamada para reserva {}: no-op", reservaVacanteId);
            return false;
        }

        reservaVacante.setNotificacionPago(true);
        reservaVacanteRepository.updateNotificacionPago(reservaVacante, reservaVacanteId);
        log.info("Notificación de pago reclamada para reserva {}", reservaVacanteId);
        return true;
    }

    @Override
    @Transactional
    public void liberar(UUID reservaVacanteId) {
        ReservaVacante reservaVacante = reservaVacanteRepository.findWithLockByReservaVacanteId(reservaVacanteId)
                .orElseThrow(() -> new ReservaVacanteException(reservaVacanteId));

        reservaVacante.setNotificacionPago(false);
        reservaVacanteRepository.updateNotificacionPago(reservaVacante, reservaVacanteId);
        log.warn("Notificación de pago liberada para reserva {}: el envío falló y Kafka va a reintentar", reservaVacanteId);
    }

}
