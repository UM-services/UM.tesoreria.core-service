package um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper.RolMapper;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.domain.model.UsuarioRol;
import um.tesoreria.core.hexagonal.usuarios.usuarioRol.infrastructure.persistence.entity.UsuarioRolEntity;

@Component
@RequiredArgsConstructor
public class UsuarioRolMapper {

    // Excepción cross-slice autorizada: reutiliza el mapper de usuarios.rol.
    private final RolMapper rolMapper;

    public UsuarioRol toDomain(UsuarioRolEntity entity) {
        if (entity == null) return null;
        return UsuarioRol.builder()
                .usuarioRolId(entity.getUsuarioRolId())
                .userId(entity.getUserId())
                .rolId(entity.getRolId())
                .created(entity.getCreated())
                .updated(entity.getUpdated())
                .rol(rolMapper.toDomain(entity.getRol()))
                .build();
    }

    public UsuarioRolEntity toEntity(UsuarioRol domain) {
        if (domain == null) return null;
        return UsuarioRolEntity.builder()
                .usuarioRolId(domain.getUsuarioRolId())
                .userId(domain.getUserId())
                .rolId(domain.getRolId())
                .build();
    }
}
