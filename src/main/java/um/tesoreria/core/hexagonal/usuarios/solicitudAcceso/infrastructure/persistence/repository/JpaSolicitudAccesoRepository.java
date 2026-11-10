package um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.solicitudAcceso.infrastructure.persistence.entity.SolicitudAccesoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaSolicitudAccesoRepository
        extends JpaRepository<SolicitudAccesoEntity, Long> {

    Optional<SolicitudAccesoEntity> findBySolicitudAccesoId(Long solicitudAccesoId);

    List<SolicitudAccesoEntity> findAllByEstadoOrderByCreatedDesc(String estado);

    List<SolicitudAccesoEntity> findAllByUserIdOrderByCreatedDesc(Long userId);

    boolean existsByUserIdAndClaveAndEstado(Long userId, String clave, String estado);
}
