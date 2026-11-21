package um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.domain.model.ProveedorArticulo;
import um.tesoreria.core.hexagonal.compras.proveedorArticulo.infrastructure.persistence.entity.ProveedorArticuloEntity;

@Component
public class ProveedorArticuloMapper {

    public ProveedorArticuloEntity toEntity(ProveedorArticulo domain) {
        if (domain == null) {
            return null;
        }
        return ProveedorArticuloEntity.builder()
                .proveedorArticuloId(domain.proveedorArticuloId())
                .proveedorMovimientoId(domain.proveedorMovimientoId())
                .orden(domain.orden())
                .articuloId(domain.articuloId())
                .cantidad(domain.cantidad())
                .concepto(domain.concepto())
                .precioUnitario(domain.precioUnitario())
                .precioFinal(domain.precioFinal())
                .entregado(domain.entregado())
                .asignado(domain.asignado())
                .build();
    }

    public ProveedorArticulo toDomain(ProveedorArticuloEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProveedorArticulo(
                entity.getProveedorArticuloId(),
                entity.getProveedorMovimientoId(),
                entity.getOrden(),
                entity.getArticuloId(),
                entity.getCantidad(),
                entity.getConcepto(),
                entity.getPrecioUnitario(),
                entity.getPrecioFinal(),
                entity.getEntregado(),
                entity.getAsignado());
    }
}
