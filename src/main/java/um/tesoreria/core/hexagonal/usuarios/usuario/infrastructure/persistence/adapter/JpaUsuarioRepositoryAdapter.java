package um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.usuario.application.exception.UsuarioException;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.model.Usuario;
import um.tesoreria.core.hexagonal.usuarios.usuario.domain.ports.out.UsuarioRepository;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.entity.UsuarioEntity;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.mapper.UsuarioMapper;
import um.tesoreria.core.hexagonal.usuarios.usuario.infrastructure.persistence.repository.JpaUsuarioRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    public List<Usuario> findUsuariosBySearch(String texto) {
        return jpaUsuarioRepository.findUsuariosBySearch(texto).stream()
                .map(usuarioMapper::toDomainModel)
                .collect(Collectors.toList());
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
    @Transactional
    public Usuario save(Usuario usuario) {
        UsuarioEntity saved;
        if (usuario.getUserId() != null) {
            // Update: trabajar sobre la entidad managed en vez de hacer merge de una
            // entidad partial-built evita pisar columnas que el dominio no modela.
            UsuarioEntity managed = jpaUsuarioRepository.findByUserId(usuario.getUserId())
                    .orElseThrow(() -> new UsuarioException(usuario.getUserId()));
            usuarioMapper.updateEntity(usuario, managed);
            saved = managed;
        } else {
            saved = jpaUsuarioRepository.save(usuarioMapper.toEntity(usuario));
        }
        return usuarioMapper.toDomainModel(saved);
    }

    @Override
    public void updateLastLog(Long userId, OffsetDateTime lastLog) {
        jpaUsuarioRepository.updateLastLog(userId, lastLog);
    }
}
