package um.tesoreria.core.hexagonal.ubicacion.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacion.domain.model.Ubicacion;
import um.tesoreria.core.hexagonal.ubicacion.infrastructure.web.dto.UbicacionResponse;

@Component
public class UbicacionDtoMapper {
    public UbicacionResponse toResponse(Ubicacion domain) {
        if (domain == null) return null;
        return UbicacionResponse.builder()
                .ubicacionId(domain.getUbicacionId())
                .nombre(domain.getNombre())
                .dependenciaId(domain.getDependenciaId())
                .geograficaId(domain.getGeograficaId())
                .build();
    }
}
