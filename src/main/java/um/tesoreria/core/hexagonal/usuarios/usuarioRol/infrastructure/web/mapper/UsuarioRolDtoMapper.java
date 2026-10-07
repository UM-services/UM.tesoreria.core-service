package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper.RolDtoMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.dto.UsuarioRolRequest;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.web.dto.UsuarioRolResponse;

@Component
@RequiredArgsConstructor
public class UsuarioRolDtoMapper {

    // Excepción cross-slice autorizada: reutiliza el DTO mapper de usuarios.rol.
    private final RolDtoMapper rolDtoMapper;

    public UsuarioRol toDomain(UsuarioRolRequest request) {
        if (request == null) return null;
        return UsuarioRol.builder()
                .userId(request.getUserId())
                .rolId(request.getRolId())
                .build();
    }

    public UsuarioRolResponse toResponse(UsuarioRol domain) {
        if (domain == null) return null;
        return UsuarioRolResponse.builder()
                .usuarioRolId(domain.getUsuarioRolId())
                .userId(domain.getUserId())
                .rolId(domain.getRolId())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .rol(rolDtoMapper.toResponse(domain.getRol()))
                .build();
    }
}
