package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity.PermisoEntity;

import java.util.Optional;

@Repository
public interface JpaPermisoRepository extends JpaRepository<PermisoEntity, Long> {

    Optional<PermisoEntity> findByPermisoId(Long permisoId);

    Optional<PermisoEntity> findByAplicacionAndClave(String aplicacion, String clave);

    boolean existsByPermisoId(Long permisoId);

    void deleteByPermisoId(Long permisoId);
}
