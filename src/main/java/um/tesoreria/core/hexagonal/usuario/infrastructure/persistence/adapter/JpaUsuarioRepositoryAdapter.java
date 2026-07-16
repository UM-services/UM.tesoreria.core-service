package um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuario.domain.ports.out.UsuarioRepository;
import um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.entity.UsuarioEntity;
import um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.mapper.UsuarioMapper;
import um.tesoreria.core.hexagonal.usuario.infrastructure.persistence.repository.JpaUsuarioRepository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUsuarioRepositoryAdapter implements UsuarioRepository {

    private final JpaUsuarioRepository jpaUsuarioRepository;
    private final UsuarioMapper usuarioMapper;

    @Override
    public Optional<Usuario> findByLogin(String login) {
        return jpaUsuarioRepository.findByLogin(login).map(usuarioMapper::toDomainModel);
    }

    @Override
    public Optional<Usuario> findByPassword(String password) {
        return jpaUsuarioRepository.findByPassword(password).map(usuarioMapper::toDomainModel);
    }

    @Override
    public Optional<Usuario> findByUserId(Long userId) {
        return jpaUsuarioRepository.findByUserId(userId).map(usuarioMapper::toDomainModel);
    }

    @Override
    public Optional<Usuario> findByGoogleMailAndActivo(String googleMail, Byte activo) {
        return jpaUsuarioRepository.findByGoogleMailAndActivo(googleMail, activo).map(usuarioMapper::toDomainModel);
    }

    @Override
    public Usuario save(Usuario usuario) {
        UsuarioEntity entity = usuarioMapper.toEntity(usuario);
        UsuarioEntity saved = jpaUsuarioRepository.save(entity);
        return usuarioMapper.toDomainModel(saved);
    }

    @Override
    public void updateLastLog(Long userId, OffsetDateTime lastLog) {
        jpaUsuarioRepository.updateLastLog(userId, lastLog);
    }
}
