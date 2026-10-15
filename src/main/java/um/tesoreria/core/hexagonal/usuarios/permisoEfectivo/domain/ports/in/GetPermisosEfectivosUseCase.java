package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;

public interface GetPermisosEfectivosUseCase {
    PermisoEfectivo getPermisosEfectivos(Long userId);
}
