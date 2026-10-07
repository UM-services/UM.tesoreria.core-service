package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.Permiso;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoRequest;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.PermisoResponse;

@Component
public class PermisoDtoMapper {

    public Permiso toDomain(PermisoRequest request) {
        if (request == null) return null;
        Permiso.PermisoBuilder builder = Permiso.builder()
                .clave(request.getClave())
                .descripcion(request.getDescripcion())
                .modulo(request.getModulo());
        if (request.getAplicacion() != null) builder.aplicacion(request.getAplicacion());
        if (request.getActivo() != null) builder.activo(request.getActivo());
        return builder.build();
    }

    public PermisoResponse toResponse(Permiso domain) {
        if (domain == null) return null;
        return PermisoResponse.builder()
                .permisoId(domain.getPermisoId())
                .clave(domain.getClave())
                .descripcion(domain.getDescripcion())
                .modulo(domain.getModulo())
                .aplicacion(domain.getAplicacion())
                .activo(domain.getActivo())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }
}
