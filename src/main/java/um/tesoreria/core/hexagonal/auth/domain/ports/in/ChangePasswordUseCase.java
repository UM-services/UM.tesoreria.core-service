package um.tesoreria.core.hexagonal.auth.domain.ports.in;

import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;

public interface ChangePasswordUseCase {
    UsuarioAuth changePassword(Long userId, String login, String currentPassword, String newPassword, String reClaveNueva, String nombre);
}
