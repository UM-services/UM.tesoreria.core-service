package um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper.PermisoDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.domain.model.UsuarioPermiso;
import um.tesoreria.core.hexagonal.usuarios.usuarioPermiso.infrastructure.web.dto.UsuarioPermisoResponse;

@Component
@RequiredArgsConstructor
public class UsuarioPermisoDtoMapper {

    // Excepción cross-slice autorizada: reutiliza el DTO mapper de usuarios.permiso.
    private final PermisoDtoMapper permisoDtoMapper;

    public UsuarioPermisoResponse toResponse(UsuarioPermiso domain) {
        if (domain == null) return null;
        return UsuarioPermisoResponse.builder()
                .usuarioPermisoId(domain.getUsuarioPermisoId())
                .userId(domain.getUserId())
                .permisoId(domain.getPermisoId())
                .otorgado(domain.getOtorgado())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .permiso(permisoDtoMapper.toResponse(domain.getPermiso()))
                .build();
    }
}
