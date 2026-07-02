package um.tesoreria.core.hexagonal.lectivo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.lectivo.domain.model.Lectivo;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.web.dto.LectivoRequest;
import um.tesoreria.core.hexagonal.lectivo.infrastructure.web.dto.LectivoResponse;

@Component
public class LectivoDtoMapper {

    public Lectivo toDomain(LectivoRequest request) {
        if (request == null) return null;
        return Lectivo.builder()
                .lectivoId(request.getLectivoId())
                .nombre(request.getNombre())
                .fechaInicio(request.getFechaInicio())
                .fechaFinal(request.getFechaFinal())
                .build();
    }

    public LectivoResponse toResponse(Lectivo domain) {
        if (domain == null) return null;
        return LectivoResponse.builder()
                .lectivoId(domain.getLectivoId())
                .nombre(domain.getNombre())
                .fechaInicio(domain.getFechaInicio())
                .fechaFinal(domain.getFechaFinal())
                .build();
    }
}
