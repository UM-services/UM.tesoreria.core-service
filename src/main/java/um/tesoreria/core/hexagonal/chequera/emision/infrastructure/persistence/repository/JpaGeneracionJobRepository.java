package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.GeneracionJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.GeneracionJobEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaGeneracionJobRepository extends JpaRepository<GeneracionJobEntity, Long> {

    Optional<GeneracionJobEntity> findFirstByEstadoOrderByJobIdAsc(GeneracionJobEstado estado);

    Optional<GeneracionJobEntity> findFirstByEtapaAndEstadoInOrderByJobIdDesc(GeneracionEtapa etapa,
                                                                             List<GeneracionJobEstado> estados);

    List<GeneracionJobEntity> findAllByEstadoOrderByJobIdAsc(GeneracionJobEstado estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<GeneracionJobEntity> findWithLockByJobId(Long jobId);

    List<GeneracionJobEntity> findAllByEstadoAndHeartbeatBefore(GeneracionJobEstado estado,
                                                                OffsetDateTime heartbeatLimite);

    List<GeneracionJobEntity> findAllByEstadoInAndFinalizadoBefore(List<GeneracionJobEstado> estados,
                                                                   OffsetDateTime finalizadoLimite);
}
