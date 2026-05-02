package um.tesoreria.core.hexagonal.auth.infrastructure.persistence.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.auth.domain.model.UsuarioAuth;
import um.tesoreria.core.hexagonal.auth.domain.ports.out.UsuarioAuthRepository;
import um.tesoreria.core.hexagonal.auth.infrastructure.persistence.mapper.UsuarioAuthMapper;
import um.tesoreria.core.kotlin.model.Usuario;
import um.tesoreria.core.repository.UsuarioRepository;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JpaUsuarioAuthRepositoryAdapter implements UsuarioAuthRepository {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioAuthMapper usuarioAuthMapper;

    @Override
    public Optional<UsuarioAuth> findByLogin(String login) {
        return usuarioRepository.findByLogin(login).map(usuarioAuthMapper::toDomainModel);
    }

    @Override
    public Optional<UsuarioAuth> findByPassword(String password) {
        return usuarioRepository.findByPassword(password).map(usuarioAuthMapper::toDomainModel);
    }

    @Override
    public UsuarioAuth save(UsuarioAuth usuario) {
        Usuario entity = usuarioAuthMapper.toEntity(usuario);
        Usuario savedEntity = usuarioRepository.save(entity);
        return usuarioAuthMapper.toDomainModel(savedEntity);
    }
}
