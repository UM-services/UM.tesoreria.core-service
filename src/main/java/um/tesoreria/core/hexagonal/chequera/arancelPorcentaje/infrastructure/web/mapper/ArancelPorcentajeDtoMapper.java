package um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.domain.model.ArancelPorcentaje;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.dto.ArancelPorcentajeRequest;
import um.tesoreria.core.hexagonal.chequera.arancelPorcentaje.infrastructure.web.dto.ArancelPorcentajeResponse;

@Component
public class ArancelPorcentajeDtoMapper {

    public ArancelPorcentaje toDomain(ArancelPorcentajeRequest request) {
        if (request == null) return null;
        return ArancelPorcentaje.builder()
                .aranceltipoId(request.getAranceltipoId())
                .productoId(request.getProductoId())
                .porcentaje(request.getPorcentaje())
                .build();
    }

    public ArancelPorcentajeResponse toResponse(ArancelPorcentaje domain) {
        if (domain == null) return null;
        return ArancelPorcentajeResponse.builder()
                .arancelporcentajeId(domain.getArancelporcentajeId())
                .aranceltipoId(domain.getAranceltipoId())
                .productoId(domain.getProductoId())
                .porcentaje(domain.getPorcentaje())
                .build();
    }
}
