package um.tesoreria.core.hexagonal.chequera.producto.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.web.dto.ProductoRequest;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.web.dto.ProductoResponse;

@Component
public class ProductoDtoMapper {

    public Producto toDomain(ProductoRequest request) {
        if (request == null) return null;
        return Producto.builder()
                .productoId(request.getProductoId())
                .nombre(request.getNombre())
                .build();
    }

    public ProductoResponse toResponse(Producto domain) {
        if (domain == null) return null;
        return ProductoResponse.builder()
                .productoId(domain.getProductoId())
                .nombre(domain.getNombre())
                .build();
    }
}
