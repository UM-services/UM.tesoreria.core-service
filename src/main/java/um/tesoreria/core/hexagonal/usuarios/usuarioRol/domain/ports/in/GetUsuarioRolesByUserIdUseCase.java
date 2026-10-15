package um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;

import java.util.List;

public interface GetUsuarioRolesByUserIdUseCase {
    List<UsuarioRol> getByUserId(Long userId);
}
