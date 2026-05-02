package um.tesoreria.core.hexagonal.geografica.infrastructure.web.mapper;

import um.tesoreria.core.hexagonal.geografica.domain.model.Geografica;
import um.tesoreria.core.hexagonal.geografica.infrastructure.web.dto.GeograficaResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class GeograficaDtoMapper {

    public GeograficaResponse toResponse(Geografica domain) {
        if (domain == null) return null;
        return new GeograficaResponse(
                domain.getGeograficaId(),
                domain.getNombre(),
                domain.getSinChequera()
        );
    }

    public List<GeograficaResponse> toResponseList(List<Geografica> domains) {
        if (domains == null) return null;
        return domains.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

}
