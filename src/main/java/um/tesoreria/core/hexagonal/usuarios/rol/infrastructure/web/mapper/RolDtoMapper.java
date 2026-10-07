package um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.rol.domain.model.Rol;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolRequest;
import um.tesoreria.core.hexagonal.usuarios.rol.infrastructure.web.dto.RolResponse;

@Component
public class RolDtoMapper {

    public Rol toDomain(RolRequest request) {
        if (request == null) return null;
        Rol.RolBuilder builder = Rol.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion());
        if (request.getAplicacion() != null) builder.aplicacion(request.getAplicacion());
        if (request.getActivo() != null) builder.activo(request.getActivo());
        return builder.build();
    }

    public RolResponse toResponse(Rol domain) {
        if (domain == null) return null;
        return RolResponse.builder()
                .rolId(domain.getRolId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .aplicacion(domain.getAplicacion())
                .activo(domain.getActivo())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }
}
