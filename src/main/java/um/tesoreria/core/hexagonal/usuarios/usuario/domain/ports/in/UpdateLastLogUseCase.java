package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import java.util.Optional;

public interface UpdateLastLogUseCase {
    Optional<Usuario> updateLastLog(Long userId);
}
