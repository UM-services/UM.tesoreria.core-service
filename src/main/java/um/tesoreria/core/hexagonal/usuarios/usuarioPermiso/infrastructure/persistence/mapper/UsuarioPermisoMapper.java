package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper.PermisoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.persistence.entity.UsuarioPermisoEntity;

@Component
@RequiredArgsConstructor
public class UsuarioPermisoMapper {

    // Excepción cross-slice autorizada: reutiliza el mapper de usuarios.permiso.
    private final PermisoMapper permisoMapper;

    public UsuarioPermiso toDomain(UsuarioPermisoEntity entity) {
        if (entity == null) return null;
        return UsuarioPermiso.builder()
                .usuarioPermisoId(entity.getUsuarioPermisoId())
                .userId(entity.getUserId())
                .permisoId(entity.getPermisoId())
                .otorgado(entity.getOtorgado())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .permiso(permisoMapper.toDomain(entity.getPermiso()))
                .build();
    }

    public UsuarioPermisoEntity toEntity(UsuarioPermiso domain) {
        if (domain == null) return null;
        return UsuarioPermisoEntity.builder()
                .usuarioPermisoId(domain.getUsuarioPermisoId())
                .userId(domain.getUserId())
                .permisoId(domain.getPermisoId())
                .otorgado(domain.getOtorgado())
                .build();
    }
}
