package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;

public interface SetUsuarioPermisoUseCase {
    UsuarioPermiso setUsuarioPermiso(Long userId, Long permisoId, Byte otorgado);
}
