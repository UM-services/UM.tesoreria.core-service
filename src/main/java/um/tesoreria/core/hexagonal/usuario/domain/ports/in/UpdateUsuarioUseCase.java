package um.tesoreria.core.hexagonal.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;

public interface UpdateUsuarioUseCase {
    Usuario updateUsuario(Usuario usuario, Long userId);
}
