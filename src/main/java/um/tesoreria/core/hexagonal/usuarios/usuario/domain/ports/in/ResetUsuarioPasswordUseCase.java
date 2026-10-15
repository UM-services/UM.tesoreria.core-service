package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.util.Optional;

/**
 * Resetea la clave de un usuario sin exigir la clave anterior (administración).
 * La clave llega en texto plano y se persiste hasheada.
 */
public interface ResetUsuarioPasswordUseCase {
    Optional<Usuario> resetPassword(Long userId, String newPassword);
}
