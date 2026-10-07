package um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;

public interface CreateRolPermisoUseCase {
    RolPermiso createRolPermiso(RolPermiso rolPermiso);
}
