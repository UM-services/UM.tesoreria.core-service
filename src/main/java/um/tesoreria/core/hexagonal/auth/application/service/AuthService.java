package um.tesoreria.core.hexagonal.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.domain.ports.in.LoginUseCase;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginUseCase loginUseCase;

    public UsuarioAuth login(String login, String password) {
        return loginUseCase.login(login, password);
    }
}
