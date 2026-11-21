package um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.domain.model.ProveedorValor;
import um.tesoreria.core.hexagonal.pagos.proveedorValor.infrastructure.persistence.entity.ProveedorValorEntity;

@Component
public class ProveedorValorMapper {

    public ProveedorValorEntity toEntity(ProveedorValor domain) {
        if (domain == null) {
            return null;
        }
        return ProveedorValorEntity.builder()
                .proveedorValorId(domain.proveedorValorId())
                .proveedorMovimientoId(domain.proveedorMovimientoId())
                .orden(domain.orden())
                .valorMovimientoId(domain.valorMovimientoId())
                .build();
    }

    public ProveedorValor toDomain(ProveedorValorEntity entity) {
        if (entity == null) {
            return null;
        }
        return new ProveedorValor(
                entity.getProveedorValorId(),
                entity.getProveedorMovimientoId(),
                entity.getOrden(),
                entity.getValorMovimientoId()
        );
    }
}
