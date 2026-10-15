package um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;

public interface CreateUsuarioRolUseCase {
    UsuarioRol createUsuarioRol(UsuarioRol usuarioRol);
}
