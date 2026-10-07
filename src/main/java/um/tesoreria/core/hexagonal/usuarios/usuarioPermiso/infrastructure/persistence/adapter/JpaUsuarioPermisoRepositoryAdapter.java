package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.application.exception.UsuarioPermisoException;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.ports.out.UsuarioPermisoRepository;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.entity.UsuarioPermisoEntity;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.mapper.UsuarioPermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.repository.JpaUsuarioPermisoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaUsuarioPermisoRepositoryAdapter implements UsuarioPermisoRepository {

    private final JpaUsuarioPermisoRepository repository;
    private final UsuarioPermisoMapper mapper;

    @Override
    public List<UsuarioPermiso> findAllByUserId(Long userId) {
        return repository.findAllByUserId(userId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UsuarioPermiso> findByUserIdAndPermisoId(Long userId, Long permisoId) {
        return repository.findByUserIdAndPermisoId(userId, permisoId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public UsuarioPermiso save(UsuarioPermiso usuarioPermiso) {
        UsuarioPermisoEntity saved;
        if (usuarioPermiso.getUsuarioPermisoId() != null) {
            UsuarioPermisoEntity managed = repository.findById(usuarioPermiso.getUsuarioPermisoId())
                    .orElseThrow(() -> new UsuarioPermisoException(
                            "Could not find UsuarioPermiso with id: " + usuarioPermiso.getUsuarioPermisoId()));
            managed.setUserId(usuarioPermiso.getUserId());
            managed.setPermisoId(usuarioPermiso.getPermisoId());
            managed.setOtorgado(usuarioPermiso.getOtorgado());
            saved = repository.save(managed);
        } else {
            saved = repository.save(mapper.toEntity(usuarioPermiso));
        }
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByUserIdAndPermisoId(Long userId, Long permisoId) {
        repository.deleteByUserIdAndPermisoId(userId, permisoId);
    }
}
