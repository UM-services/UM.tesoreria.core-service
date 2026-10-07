package um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

public interface CreatePermisoUseCase {
    Permiso createPermiso(Permiso permiso);
}
