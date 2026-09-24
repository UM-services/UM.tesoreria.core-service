package um.tesoreria.core.hexagonal.auth.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.domain.ports.in.ChangePasswordUseCase;
import um.tesoreria.core.hexagonal.auth.domain.ports.in.LoginUseCase;
import um.tesoreria.core.hexagonal.auth.domain.ports.out.UsuarioAuthRepository;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final LoginUseCase loginUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final UsuarioAuthRepository usuarioAuthRepository;

    public UsuarioAuth login(String login, String password) {
        return loginUseCase.login(login, password);
    }

    public UsuarioAuth changePassword(Long userId, String login, String currentPassword, String newPassword, String reClaveNueva, String nombre) {
        return changePasswordUseCase.changePassword(userId, login, currentPassword, newPassword, reClaveNueva, nombre);
    }

    public UsuarioAuth findById(Long userId) {
        return usuarioAuthRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Usuario no encontrado"));
    }
}

