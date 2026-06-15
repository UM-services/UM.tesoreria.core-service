package um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.umhub.campanha.domain.model.Campanha;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.dto.CampanhaRequest;
import um.tesoreria.core.hexagonal.umhub.campanha.infrastructure.web.dto.CampanhaResponse;

@Component
public class CampanhaDtoMapper {

    public Campanha toDomain(CampanhaRequest request) {
        if (request == null) return null;
        return Campanha.builder()
                .campanhaId(request.getCampanhaId())
                .nombre(request.getNombre())
                .activa(request.getActiva())
                .valorReserva(request.getValorReserva())
                .build();
    }

    public CampanhaResponse toResponse(Campanha domain) {
        if (domain == null) return null;
        return CampanhaResponse.builder()
                .campanhaId(domain.getCampanhaId())
                .nombre(domain.getNombre())
                .activa(domain.getActiva())
                .valorReserva(domain.getValorReserva())
                .created(domain.getCreated())
                .build();
    }
}
