package um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out;

import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByLogin(String login);

    /** Usuarios activos cuyo login o nombre contiene el texto; con texto nulo, todos los activos. */
    List<Usuario> findUsuariosBySearch(String texto);
    Optional<Usuario> findByPassword(String password);
    Optional<Usuario> findByUserId(Long userId);
    Optional<Usuario> findByGoogleMailAndActivo(String googleMail, Byte activo);
    Usuario save(Usuario usuario);
    void updateLastLog(Long userId, OffsetDateTime lastLog);
}
