package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.util.Optional;

/**
 * Actualiza la configuración administrable de un usuario (datos y flags) sin
 * tocar login ni clave.
 */
public interface UpdateUsuarioConfiguracionUseCase {
    Optional<Usuario> updateConfiguracion(Usuario cambios, Long userId);
}
