package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.in;

import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.util.Optional;

/** Habilita (activo = 1) o deshabilita (activo = 0) un usuario. */
public interface UpdateUsuarioEstadoUseCase {
    Optional<Usuario> updateEstado(Long userId, Byte activo);
}
