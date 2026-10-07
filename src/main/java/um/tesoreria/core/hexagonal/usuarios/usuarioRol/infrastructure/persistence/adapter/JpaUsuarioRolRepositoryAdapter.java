package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.ports.out.UsuarioRolRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.mapper.UsuarioRolMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.repository.JpaUsuarioRolRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUsuarioRolRepositoryAdapter implements UsuarioRolRepository {

    private final JpaUsuarioRolRepository repository;
    private final UsuarioRolMapper mapper;

    @Override
    public List<UsuarioRol> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioRol> findByUserIdAndRolId(Long userId, Long rolId) {
        return repository.findByUserIdAndRolId(userId, rolId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public UsuarioRol save(UsuarioRol usuarioRol) {
        return mapper.toDomain(repository.save(mapper.toEntity(usuarioRol)));
    }

    @Override
    @Transactional
    public void deleteByUserIdAndRolId(Long userId, Long rolId) {
        repository.deleteByUserIdAndRolId(userId, rolId);
    }
}
