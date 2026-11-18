package um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasEtapa;
import um.tesoreria.core.hexagonal.chequera.emision.domain.model.IncompletasJobEstado;
import um.tesoreria.core.hexagonal.chequera.emision.infrastructure.persistence.entity.IncompletasJobEntity;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaIncompletasJobRepository extends JpaRepository<IncompletasJobEntity, Long> {

    Optional<IncompletasJobEntity> findFirstByEstadoOrderByJobIdAsc(IncompletasJobEstado estado);

    Optional<IncompletasJobEntity> findFirstByEtapaAndEstadoInOrderByJobIdDesc(IncompletasEtapa etapa,
                                                                              List<IncompletasJobEstado> estados);

    List<IncompletasJobEntity> findAllByEstadoOrderByJobIdAsc(IncompletasJobEstado estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<IncompletasJobEntity> findWithLockByJobId(Long jobId);

    List<IncompletasJobEntity> findAllByEstadoAndHeartbeatBefore(IncompletasJobEstado estado,
                                                                OffsetDateTime heartbeatLimite);

    List<IncompletasJobEntity> findAllByEstadoInAndFinalizadoBefore(List<IncompletasJobEstado> estados,
                                                                   OffsetDateTime finalizadoLimite);

    long countByEstado(IncompletasJobEstado estado);
}
