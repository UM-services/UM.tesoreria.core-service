package um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.usuarios.permiso.domain.model.SyncPermisosResultado;
import um.tesoreria.core.hexagonal.usuarios.permiso.infrastructure.web.dto.SyncPermisosResponse;

@Component
public class SyncPermisosDtoMapper {

    public SyncPermisosResponse toResponse(SyncPermisosResultado domain) {
        if (domain == null) return null;
        return SyncPermisosResponse.builder()
                .dryRun(domain.isDryRun())
                .totalManifiesto(domain.getTotalManifiesto())
                .agregados(domain.getAgregados())
                .actualizados(domain.getActualizados())
                .retirados(domain.getRetirados())
                .sinCambios(domain.getSinCambios())
                .clavesAgregadas(domain.getClavesAgregadas())
                .clavesActualizadas(domain.getClavesActualizadas())
                .clavesRetiradas(domain.getClavesRetiradas())
                .build();
    }
}
