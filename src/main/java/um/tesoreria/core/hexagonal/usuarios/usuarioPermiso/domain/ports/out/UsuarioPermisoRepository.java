package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;

import java.util.List;
import java.util.Optional;

public interface UsuarioPermisoRepository {
    List<UsuarioPermiso> findAllByUserId(Long userId);

    Optional<UsuarioPermiso> findByUserIdAndPermisoId(Long userId, Long permisoId);

    UsuarioPermiso save(UsuarioPermiso usuarioPermiso);

    void deleteByUserIdAndPermisoId(Long userId, Long permisoId);
}
