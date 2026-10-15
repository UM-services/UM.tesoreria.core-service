package um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;

import java.util.List;
import java.util.Optional;

public interface RolPermisoRepository {
    List<RolPermiso> findAllByRolId(Long rolId);

    Optional<RolPermiso> findByRolIdAndPermisoId(Long rolId, Long permisoId);

    RolPermiso save(RolPermiso rolPermiso);

    void deleteByRolIdAndPermisoId(Long rolId, Long permisoId);
}
