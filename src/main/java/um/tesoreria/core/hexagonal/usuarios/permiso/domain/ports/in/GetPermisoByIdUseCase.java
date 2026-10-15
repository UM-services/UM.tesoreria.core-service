package um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;

import java.util.Optional;

public interface GetPermisoByIdUseCase {
    Optional<Permiso> getPermisoById(Long permisoId);
}
