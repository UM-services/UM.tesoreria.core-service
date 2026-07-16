package um.tesoreria.core.hexagonal.usuario.domain.ports.out;

import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface UsuarioRepository {
    Optional<Usuario> findByLogin(String login);
    Optional<Usuario> findByPassword(String password);
    Optional<Usuario> findByUserId(Long userId);
    Optional<Usuario> findByGoogleMailAndActivo(String googleMail, Byte activo);
    Usuario save(Usuario usuario);
    void updateLastLog(Long userId, OffsetDateTime lastLog);
}
