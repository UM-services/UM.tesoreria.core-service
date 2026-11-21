package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.envio.domain.model.EnvioJobEstado;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioJobEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaEnvioJobRepository extends JpaRepository<EnvioJobEntity, Long> {

    Optional<EnvioJobEntity> findFirstByEstadoOrderByJobIdAsc(EnvioJobEstado estado);

    Optional<EnvioJobEntity> findFirstByEstadoInOrderByJobIdAsc(List<EnvioJobEstado> estados);

    List<EnvioJobEntity> findAllByEstadoOrderByJobIdAsc(EnvioJobEstado estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EnvioJobEntity> findWithLockByJobId(Long jobId);

    List<EnvioJobEntity> findAllByEstadoAndHeartbeatBefore(EnvioJobEstado estado, OffsetDateTime heartbeatLimite);

    List<EnvioJobEntity> findAllByEstadoInAndFinalizadoBefore(List<EnvioJobEstado> estados,
                                                              OffsetDateTime finalizadoLimite);

    long countByEstado(EnvioJobEstado estado);
}
