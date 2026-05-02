package um.tesoreria.core.hexagonal.auth.domain.ports.out;

import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import java.util.Optional;

public interface UsuarioAuthRepository {
    Optional<UsuarioAuth> findByLogin(String login);
    Optional<UsuarioAuth> findByPassword(String password);
    UsuarioAuth save(UsuarioAuth usuario);
}
