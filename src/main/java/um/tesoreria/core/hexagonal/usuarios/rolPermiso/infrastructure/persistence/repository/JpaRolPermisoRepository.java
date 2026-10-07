package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.entity.RolPermisoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRolPermisoRepository extends JpaRepository<RolPermisoEntity, Long> {

    List<RolPermisoEntity> findAllByRolId(Long rolId);

    Optional<RolPermisoEntity> findByRolIdAndPermisoId(Long rolId, Long permisoId);

    void deleteByRolIdAndPermisoId(Long rolId, Long permisoId);
}
