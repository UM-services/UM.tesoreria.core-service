package um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.ports.out;

import um.tesoreria.core.hexagonal.umhub.reservaVacante.domain.model.ReservaVacante;

import java.util.Optional;
import java.util.UUID;

public interface ReservaVacanteRepository {
    ReservaVacante create(ReservaVacante reservaVacante);
    Optional<ReservaVacante> findByReservaVacanteId(UUID reservaVacanteId);

    /**
     * Igual que {@link #findByReservaVacanteId(UUID)} pero con {@code SELECT ... FOR UPDATE}: es el
     * primitivo del claim de notificación. Tiene que llamarse dentro de una transacción.
     */
    Optional<ReservaVacante> findWithLockByReservaVacanteId(UUID reservaVacanteId);

    ReservaVacante update(ReservaVacante reservaVacante, UUID reservaVacanteId);

    /**
     * Persiste únicamente la marca de notificación de pago, sin tocar estado/importe/vencimiento
     * (que sí reescribe {@link #update(ReservaVacante, UUID)}).
     */
    ReservaVacante updateNotificacionPago(ReservaVacante reservaVacante, UUID reservaVacanteId);
}
