package um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.domain.model.AsientoIngresoJobEstado;
import um.tesoreria.core.hexagonal.contable.asientoIngreso.infrastructure.persistence.entity.AsientoIngresoJobEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaAsientoIngresoJobRepository extends JpaRepository<AsientoIngresoJobEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AsientoIngresoJobEntity> findFirstByEstadoOrderByJobIdAsc(AsientoIngresoJobEstado estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<AsientoIngresoJobEntity> findWithLockByJobId(Long jobId);

    List<AsientoIngresoJobEntity> findAllByEstadoAndHeartbeatBefore(AsientoIngresoJobEstado estado,
                                                                    OffsetDateTime heartbeat);

    List<AsientoIngresoJobEntity> findAllByEstadoInAndFinalizadoBefore(List<AsientoIngresoJobEstado> estados,
                                                                       OffsetDateTime finalizado);

}
