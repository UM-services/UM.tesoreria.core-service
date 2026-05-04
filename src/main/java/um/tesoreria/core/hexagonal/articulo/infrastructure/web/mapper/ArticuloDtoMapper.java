package um.tesoreria.core.hexagonal.articulo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.articulo.domain.model.Articulo;
import um.tesoreria.core.hexagonal.articulo.domain.model.ArticuloSearch;
import um.tesoreria.core.hexagonal.articulo.infrastructure.web.dto.ArticuloRequest;
import um.tesoreria.core.hexagonal.articulo.infrastructure.web.dto.ArticuloResponse;
import um.tesoreria.core.hexagonal.articulo.infrastructure.web.dto.ArticuloSearchResponse;

@Component
public class ArticuloDtoMapper {

    public Articulo toDomain(ArticuloRequest request) {
        if (request == null) return null;
        return Articulo.builder()
                .nombre(request.getNombre())
                .descripcion(request.getDescripcion())
                .unidad(request.getUnidad())
                .precio(request.getPrecio())
                .inventariable(request.getInventariable())
                .stockMinimo(request.getStockMinimo())
                .numeroCuenta(request.getNumeroCuenta())
                .tipo(request.getTipo())
                .directo(request.getDirecto())
                .habilitado(request.getHabilitado())
                .build();
    }

    public ArticuloResponse toResponse(Articulo domain) {
        if (domain == null) return null;
        return ArticuloResponse.builder()
                .articuloId(domain.getArticuloId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .unidad(domain.getUnidad())
                .precio(domain.getPrecio())
                .inventariable(domain.getInventariable())
                .stockMinimo(domain.getStockMinimo())
                .numeroCuenta(domain.getNumeroCuenta())
                .tipo(domain.getTipo())
                .directo(domain.getDirecto())
                .habilitado(domain.getHabilitado())
                .cuenta(domain.getCuenta())
                .build();
    }

    public ArticuloSearchResponse toSearchResponse(ArticuloSearch domain) {
        if (domain == null) return null;
        return ArticuloSearchResponse.builder()
                .articuloId(domain.getArticuloId())
                .nombre(domain.getNombre())
                .descripcion(domain.getDescripcion())
                .unidad(domain.getUnidad())
                .precio(domain.getPrecio())
                .inventariable(domain.getInventariable())
                .stockMinimo(domain.getStockMinimo())
                .cuenta(domain.getCuenta())
                .tipo(domain.getTipo())
                .directo(domain.getDirecto())
                .habilitado(domain.getHabilitado())
                .search(domain.getSearch())
                .build();
    }
}
