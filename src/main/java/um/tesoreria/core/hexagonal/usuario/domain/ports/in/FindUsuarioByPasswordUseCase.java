package um.tesoreria.core.hexagonal.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import java.util.Optional;

public interface FindUsuarioByPasswordUseCase {
    Optional<Usuario> findUsuarioByPassword(String password);
}
