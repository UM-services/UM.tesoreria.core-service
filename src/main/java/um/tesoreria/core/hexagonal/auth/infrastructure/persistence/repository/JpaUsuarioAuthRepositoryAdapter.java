package um.tesoreria.core.hexagonal.auth.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.domain.ports.out.UsuarioAuthRepository;
import um.tesoreria.core.hexagonal.auth.infrastructure.persistence.mapper.UsuarioAuthMapper;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.repository.JpaUsuarioRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUsuarioAuthRepositoryAdapter implements UsuarioAuthRepository {

    private final JpaUsuarioRepository usuarioRepository;
    private final UsuarioAuthMapper usuarioAuthMapper;

    @Override
    public Optional<UsuarioAuth> findById(Long userId) {
        return usuarioRepository.findById(userId).map(usuarioAuthMapper::toDomainModel);
    }

    @Override
    public Optional<UsuarioAuth> findByLogin(String login) {
        return usuarioRepository.findByLogin(login).map(usuarioAuthMapper::toDomainModel);
    }

    @Override
    public Optional<UsuarioAuth> findByPassword(String password) {
        return usuarioRepository.findByPassword(password).map(usuarioAuthMapper::toDomainModel);
    }

    @Override
    @Transactional
    public void updateLastLog(Long userId, OffsetDateTime lastLog) {
        usuarioRepository.updateLastLog(userId, lastLog);
    }

    @Override
    @Transactional
    public void updateCredentials(Long userId, String password, String nombre) {
        UsuarioEntity managed = usuarioRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("ERROR: Usuario NO Encontrado"));
        managed.setPassword(password);
        if (nombre != null) {
            managed.setNombre(nombre);
        }
        // dirty checking: el UPDATE toca solo password (y nombre si vino). El resto de las
        // columnas queda intacto por definicion, incluso cuando se agreguen nuevas.
    }
}
