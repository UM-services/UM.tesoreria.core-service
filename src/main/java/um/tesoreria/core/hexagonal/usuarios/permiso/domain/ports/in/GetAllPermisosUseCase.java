package um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

import java.util.List;

public interface GetAllPermisosUseCase {
    List<Permiso> getAllPermisos();
}
