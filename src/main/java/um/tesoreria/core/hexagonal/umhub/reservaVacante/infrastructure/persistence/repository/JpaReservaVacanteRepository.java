package um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.umhub.reservaVacante.infrastructure.persistence.entity.ReservaVacanteEntity;

import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaReservaVacanteRepository extends JpaRepository<ReservaVacanteEntity, UUID> {

    Optional<ReservaVacanteEntity> findByReservaVacanteId(UUID reservaVacanteId);

    /**
     * Claim de la notificación de pago: el lock se mantiene hasta que termina la transacción del
     * caso de uso, así dos consumidores concurrentes no pueden notificar el mismo pago. Bajo
     * REPEATABLE READ el segundo lee la versión ya commiteada y ve el flag en 1.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<ReservaVacanteEntity> findWithLockByReservaVacanteId(UUID reservaVacanteId);

}
