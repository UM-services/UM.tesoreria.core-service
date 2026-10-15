package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;

import java.util.List;

public interface GetUsuarioPermisosByUserIdUseCase {
    List<UsuarioPermiso> getByUserId(Long userId);
}
