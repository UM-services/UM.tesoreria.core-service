package um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.domain.model.GuaraniUbicacion;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.dto.GuaraniUbicacionRequest;
import um.tesoreria.core.hexagonal.guarani.guaraniUbicacion.infrastructure.web.dto.GuaraniUbicacionResponse;

@Component
public class GuaraniUbicacionDtoMapper {
    public GuaraniUbicacion toDomain(GuaraniUbicacionRequest request) {
        if (request == null) {
            return null;
        }
        return GuaraniUbicacion.builder()
                .ubicacion(request.getUbicacion())
                .geograficaId(request.getGeograficaId())
                .build();
    }

    public GuaraniUbicacionResponse toResponse(GuaraniUbicacion domain) {
        if (domain == null) {
            return null;
        }
        return GuaraniUbicacionResponse.builder()
                .guaraniUbicacionId(domain.getGuaraniUbicacionId())
                .ubicacion(domain.getUbicacion())
                .geograficaId(domain.getGeograficaId())
                .build();
    }
}
