package um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.chequera.envio.infrastructure.persistence.entity.EnvioSolicitudEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface JpaEnvioSolicitudRepository extends JpaRepository<EnvioSolicitudEntity, UUID> {

    Optional<EnvioSolicitudEntity> findBySolicitudId(UUID solicitudId);

    Optional<EnvioSolicitudEntity> findByChequeraId(Long chequeraId);

    Optional<EnvioSolicitudEntity> findByDedupeKey(String dedupeKey);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<EnvioSolicitudEntity> findWithLockBySolicitudId(UUID solicitudId);

}
