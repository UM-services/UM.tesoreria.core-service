package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.EntregaDetalle;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.entity.EntregaDetalleEntity;

@Component
public class EntregaDetalleMapper {

    public EntregaDetalleEntity toEntity(EntregaDetalle domain) {
        if (domain == null) {
            return null;
        }
        return EntregaDetalleEntity.builder()
                .entregaDetalleId(domain.entregaDetalleId())
                .entregaId(domain.entregaId())
                .articuloId(domain.articuloId())
                .proveedorMovimientoId(domain.proveedorMovimientoId())
                .orden(domain.orden())
                .concepto(domain.concepto())
                .cantidad(domain.cantidad())
                .trackId(domain.trackId())
                .proveedorArticuloId(domain.proveedorArticuloId())
                .build();
    }

    public EntregaDetalle toDomain(EntregaDetalleEntity entity) {
        if (entity == null) {
            return null;
        }
        return new EntregaDetalle(
                entity.getEntregaDetalleId(),
                entity.getEntregaId(),
                entity.getArticuloId(),
                entity.getProveedorMovimientoId(),
                entity.getOrden(),
                entity.getConcepto(),
                entity.getCantidad(),
                entity.getTrackId(),
                entity.getProveedorArticuloId()
        );
    }
}
