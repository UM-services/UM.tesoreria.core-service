package um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.chequera.producto.domain.model.Producto;
import um.tesoreria.core.hexagonal.chequera.producto.infrastructure.persistence.entity.ProductoEntity;

@Component
public class ProductoMapper {

    public Producto toDomainModel(ProductoEntity entity) {
        if (entity == null) return null;
        return Producto.builder()
                .productoId(entity.getProductoId())
                .nombre(entity.getNombre())
                .build();
    }

    public ProductoEntity toEntity(Producto domain) {
        if (domain == null) return null;
        return ProductoEntity.builder()
                .productoId(domain.getProductoId())
                .nombre(domain.getNombre())
                .build();
    }
}
