package um.tesoreria.core.hexagonal.auth.domain.ports.in;

import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;

public interface LoginUseCase {
    UsuarioAuth login(String login, String password);
}
