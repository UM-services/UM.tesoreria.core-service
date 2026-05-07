package um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.ubicacionArticulo.domain.model.UbicacionArticulo;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloRequest;
import um.tesoreria.core.hexagonal.ubicacionArticulo.infrastructure.web.dto.UbicacionArticuloResponse;

@Component
public class UbicacionArticuloDtoMapper {
    public UbicacionArticulo toDomain(UbicacionArticuloRequest request) {
        if (request == null) return null;
        return UbicacionArticulo.builder()
                .ubicacionId(request.getUbicacionId())
                .articuloId(request.getArticuloId())
                .numeroCuenta(request.getNumeroCuenta())
                .build();
    }

    public UbicacionArticuloResponse toResponse(UbicacionArticulo domain) {
        if (domain == null) return null;
        return UbicacionArticuloResponse.builder()
                .ubicacionArticuloId(domain.getUbicacionArticuloId())
                .ubicacionId(domain.getUbicacionId())
                .articuloId(domain.getArticuloId())
                .numeroCuenta(domain.getNumeroCuenta())
                .ubicacion(domain.getUbicacion())
                .articulo(domain.getArticulo())
                .cuenta(domain.getCuenta())
                .build();
    }
}
