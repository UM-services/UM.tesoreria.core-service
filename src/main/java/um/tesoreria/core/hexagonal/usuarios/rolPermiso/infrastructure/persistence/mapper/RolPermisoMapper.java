package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper.PermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper.RolMapper;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.persistence.entity.RolPermisoEntity;

@Component
@RequiredArgsConstructor
public class RolPermisoMapper {

    // Excepción cross-slice autorizada: reutiliza los mappers de usuarios.rol y usuarios.permiso.
    private final RolMapper rolMapper;
    private final PermisoMapper permisoMapper;

    public RolPermiso toDomain(RolPermisoEntity entity) {
        if (entity == null) return null;
        return RolPermiso.builder()
                .rolPermisoId(entity.getRolPermisoId())
                .rolId(entity.getRolId())
                .permisoId(entity.getPermisoId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .rol(rolMapper.toDomain(entity.getRol()))
                .permiso(permisoMapper.toDomain(entity.getPermiso()))
                .build();
    }

    public RolPermisoEntity toEntity(RolPermiso domain) {
        if (domain == null) return null;
        return RolPermisoEntity.builder()
                .rolPermisoId(domain.getRolPermisoId())
                .rolId(domain.getRolId())
                .permisoId(domain.getPermisoId())
                .build();
    }
}
