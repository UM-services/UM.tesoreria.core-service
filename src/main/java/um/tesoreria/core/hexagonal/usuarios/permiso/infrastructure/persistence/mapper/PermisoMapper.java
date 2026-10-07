package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.persistence.entity.PermisoEntity;

@Component
public class PermisoMapper {

    public Permiso toDomain(PermisoEntity entity) {
        if (entity == null) return null;
        Permiso.PermisoBuilder builder = Permiso.builder()
                .permisoId(entity.getPermisoId())
                .clave(entity.getClave())
                .descripcion(entity.getDescripcion())
                .modulo(entity.getModulo())
                .created(entity.getCreated())
                .updated(entity.getUpdated());
        if (entity.getAplicacion() != null) builder.aplicacion(entity.getAplicacion());
        if (entity.getActivo() != null) builder.activo(entity.getActivo());
        return builder.build();
    }

    public PermisoEntity toEntity(Permiso domain) {
        if (domain == null) return null;
        PermisoEntity.PermisoEntityBuilder builder = PermisoEntity.builder()
                .permisoId(domain.getPermisoId())
                .clave(domain.getClave())
                .descripcion(domain.getDescripcion())
                .modulo(domain.getModulo());
        if (domain.getAplicacion() != null) builder.aplicacion(domain.getAplicacion());
        if (domain.getActivo() != null) builder.activo(domain.getActivo());
        return builder.build();
    }

    /**
     * Aplica los campos del dominio sobre una entidad YA MANAGED (cargada por id).
     * Los campos null se ignoran: "no vino = no se toca".
     */
    public void updateEntity(Permiso domain, PermisoEntity entity) {
        if (domain == null || entity == null) return;
        entity.setClave(domain.getClave());
        entity.setDescripcion(domain.getDescripcion());
        entity.setModulo(domain.getModulo());
        if (domain.getAplicacion() != null) entity.setAplicacion(domain.getAplicacion());
        if (domain.getActivo() != null) entity.setActivo(domain.getActivo());
    }
}
