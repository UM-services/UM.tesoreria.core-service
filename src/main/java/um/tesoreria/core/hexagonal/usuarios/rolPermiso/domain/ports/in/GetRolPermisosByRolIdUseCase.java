package um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;

import java.util.List;

public interface GetRolPermisosByRolIdUseCase {
    List<RolPermiso> getByRolId(Long rolId);
}
