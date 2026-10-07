package um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;

import java.util.List;
import java.util.Optional;

public interface UsuarioRolRepository {
    List<UsuarioRol> findAllByUserId(Long userId);

    Optional<UsuarioRol> findByUserIdAndRolId(Long userId, Long rolId);

    UsuarioRol save(UsuarioRol usuarioRol);

    void deleteByUserIdAndRolId(Long userId, Long rolId);
}
