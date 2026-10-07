package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.persistence.entity.RolEntity;

@Component
public class RolMapper {

    public Rol toDomain(RolEntity entity) {
        if (entity == null) return null;
        Rol.RolBuilder builder = Rol.builder()
                .rolId(entity.getRolId())
                .nombre(entity.getNombre())
                .descripcion(entity.getDescripcion())
                .created(entity.getCreated())
                .updated(entity.getUpdated());
        if (entity.getAplicacion() != null) builder.aplicacion(entity.getAplicacion());
        if (entity.getActivo() != null) builder.activo(entity.getActivo());
        return builder.build();
    }

    public RolEntity toEntity(Rol domain) {
        if (domain == null) return null;
        RolEntity.RolEntityBuilder builder = RolEntity.builder()
                .rolId(domain.getRolId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion());
        if (domain.getAplicacion() != null) builder.aplicacion(domain.getAplicacion());
        if (domain.getActivo() != null) builder.activo(domain.getActivo());
        return builder.build();
    }
}
