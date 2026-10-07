package um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper.RolDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.domain.model.RolPermiso;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.dto.RolPermisoRequest;
import um.tesoreria.core.hexagonal.usuarios.rolPermiso.infrastructure.web.dto.RolPermisoResponse;

@Component
@RequiredArgsConstructor
public class RolPermisoDtoMapper {

    // Excepción cross-slice autorizada: reutiliza los DTO mappers de usuarios.rol y usuarios.permiso.
    private final RolDtoMapper rolDtoMapper;
    private final PermisoDtoMapper permisoDtoMapper;

    public RolPermiso toDomain(RolPermisoRequest request) {
        if (request == null) return null;
        return RolPermiso.builder()
                .rolId(request.getRolId())
                .permisoId(request.getPermisoId())
                .build();
    }

    public RolPermisoResponse toResponse(RolPermiso domain) {
        if (domain == null) return null;
        return RolPermisoResponse.builder()
                .rolPermisoId(domain.getRolPermisoId())
                .rolId(domain.getRolId())
                .permisoId(domain.getPermisoId())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .rol(rolDtoMapper.toResponse(domain.getRol()))
                .permiso(permisoDtoMapper.toResponse(domain.getPermiso()))
                .build();
    }
}
