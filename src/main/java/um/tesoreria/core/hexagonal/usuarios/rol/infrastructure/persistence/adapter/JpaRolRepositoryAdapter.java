package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.rol.application.exception.RolException;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.ports.out.RolRepository;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper.RolMapper;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.repository.JpaRolRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaRolRepositoryAdapter implements RolRepository {

    private final JpaRolRepository repository;
    private final RolMapper mapper;

    @Override
    public Optional<Rol> findByRolId(Long rolId) {
        return repository.findByRolId(rolId).map(mapper::toDomain);
    }

    @Override
    public List<Rol> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Rol save(Rol rol) {
        RolEntity saved;
        if (rol.getRolId() != null) {
            RolEntity managed = repository.findByRolId(rol.getRolId())
                    .orElseGet(() -> mapper.toEntity(rol));
            managed.setNombre(rol.getNombre());
            managed.setDescripcion(rol.getDescripcion());
            if (rol.getAplicacion() != null) managed.setAplicacion(rol.getAplicacion());
            if (rol.getActivo() != null) managed.setActivo(rol.getActivo());
            saved = repository.save(managed);
        } else {
            saved = repository.save(mapper.toEntity(rol));
        }
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByRolId(Long rolId) {
        try {
            repository.deleteByRolId(rolId);
            // Fuerza el DELETE dentro de la transacción para capturar aquí el FK de
            // usuario_rol / rol_permiso si el rol todavía tiene asignaciones.
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new RolException(
                    "No se puede eliminar el rol: tiene usuarios o permisos asignados.");
        }
    }

    @Override
    public boolean existsByRolId(Long rolId) {
        return repository.existsByRolId(rolId);
    }
}
