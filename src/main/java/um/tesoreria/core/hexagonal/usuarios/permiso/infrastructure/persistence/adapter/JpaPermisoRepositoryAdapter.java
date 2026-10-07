package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.adapter;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import um.tesoreria.core.hexagonal.usuarios.permiso.application.exception.PermisoException;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.ports.out.PermisoRepository;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity.PermisoEntity;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper.PermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.repository.JpaPermisoRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JpaPermisoRepositoryAdapter implements PermisoRepository {

    private final JpaPermisoRepository repository;
    private final PermisoMapper mapper;

    @Override
    public Optional<Permiso> findByPermisoId(Long permisoId) {
        return repository.findByPermisoId(permisoId).map(mapper::toDomain);
    }

    @Override
    public Optional<Permiso> findByAplicacionAndClave(String aplicacion, String clave) {
        return repository.findByAplicacionAndClave(aplicacion, clave).map(mapper::toDomain);
    }

    @Override
    public List<Permiso> findAll() {
        return repository.findAll().stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Permiso save(Permiso permiso) {
        PermisoEntity saved;
        if (permiso.getPermisoId() != null) {
            PermisoEntity managed = repository.findByPermisoId(permiso.getPermisoId())
                    .orElseThrow(() -> new PermisoException(permiso.getPermisoId()));
            mapper.updateEntity(permiso, managed);
            saved = repository.save(managed);
        } else {
            saved = repository.save(mapper.toEntity(permiso));
        }
        return mapper.toDomain(saved);
    }

    @Override
    @Transactional
    public void deleteByPermisoId(Long permisoId) {
        try {
            repository.deleteByPermisoId(permisoId);
            // Fuerza el DELETE dentro de la transacción para capturar aquí el FK de
            // rol_permiso / usuario_permiso si el permiso todavía tiene asignaciones.
            repository.flush();
        } catch (DataIntegrityViolationException e) {
            throw new PermisoException(
                    "No se puede eliminar el permiso: tiene roles o usuarios asignados.");
        }
    }

    @Override
    public boolean existsByPermisoId(Long permisoId) {
        return repository.existsByPermisoId(permisoId);
    }
}
