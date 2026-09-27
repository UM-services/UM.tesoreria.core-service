package um.tesoreria.core.hexagonal.auth.domain.ports.out;

import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import java.time.OffsetDateTime;
import java.util.Optional;

public interface UsuarioAuthRepository {
    Optional<UsuarioAuth> findById(Long userId);
    Optional<UsuarioAuth> findByLogin(String login);
    Optional<UsuarioAuth> findByPassword(String password);

    /** Update dirigido: toca SOLO la columna last_log (evita el merge de fila completa). */
    void updateLastLog(Long userId, OffsetDateTime lastLog);

    /** Update dirigido sobre entidad managed: toca solo password y, si viene no-null, nombre. */
    void updateCredentials(Long userId, String password, String nombre);
}
