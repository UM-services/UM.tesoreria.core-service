package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.entity.UsuarioPermisoEntity;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUsuarioPermisoRepository extends JpaRepository<UsuarioPermisoEntity, Long> {

    List<UsuarioPermisoEntity> findAllByUserId(Long userId);

    Optional<UsuarioPermisoEntity> findByUserIdAndPermisoId(Long userId, Long permisoId);

    void deleteByUserIdAndPermisoId(Long userId, Long permisoId);
}
