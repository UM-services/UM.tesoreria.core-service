package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

public interface UpdateUsuarioUseCase {
    Usuario updateUsuario(Usuario usuario, Long userId);
}
