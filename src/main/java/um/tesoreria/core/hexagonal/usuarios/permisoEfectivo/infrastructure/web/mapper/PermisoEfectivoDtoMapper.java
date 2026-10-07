package um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.domain.model.PermisoEfectivo;
import um.tesoreria.core.hexagonal.usuarios.permisoEfectivo.infrastructure.web.dto.PermisoEfectivoResponse;

@Component
public class PermisoEfectivoDtoMapper {

    public PermisoEfectivoResponse toResponse(PermisoEfectivo domain) {
        if (domain == null) return null;
        return PermisoEfectivoResponse.builder()
                .userId(domain.getUserId())
                .permisos(domain.getPermisos())
                .build();
    }
}
