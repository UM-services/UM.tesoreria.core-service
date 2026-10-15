package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.ports.out.RolPermisoRepository;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.mapper.RolPermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.repository.JpaRolPermisoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaRolPermisoRepositoryAdapter implements RolPermisoRepository {

    private final JpaRolPermisoRepository repository;
    private final RolPermisoMapper mapper;

    @Override
    public List<RolPermiso> findAllByRolId(Long rolId) {
        return repository.findAllByRolId(rolId).stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<RolPermiso> findByRolIdAndPermisoId(Long rolId, Long permisoId) {
        return repository.findByRolIdAndPermisoId(rolId, permisoId).map(mapper::toDomain);
    }

    @Override
    @Transactional
    public RolPermiso save(RolPermiso rolPermiso) {
        return mapper.toDomain(repository.save(mapper.toEntity(rolPermiso)));
    }

    @Override
    @Transactional
    public void deleteByRolIdAndPermisoId(Long rolId, Long permisoId) {
        repository.deleteByRolIdAndPermisoId(rolId, permisoId);
    }
}
