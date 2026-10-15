package um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

public interface UpdatePermisoUseCase {
    Permiso updatePermiso(Permiso permiso, Long permisoId);
}
