package um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.asiento.infrastructure.persistence.entity.AsientoSecuenciaEntity;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface JpaAsientoSecuenciaRepository extends JpaRepository<AsientoSecuenciaEntity, OffsetDateTime> {

    /**
     * Bloquea la fila de la fecha. {@code findFirst...} es un derivado válido (sin {@code @Query})
     * y el lock serializa las reservas concurrentes de la misma fecha.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AsientoSecuenciaEntity> findFirstByFecha(OffsetDateTime fecha);

}
