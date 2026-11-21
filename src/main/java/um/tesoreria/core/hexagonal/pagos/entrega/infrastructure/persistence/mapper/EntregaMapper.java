package um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.pagos.entrega.domain.model.Entrega;
import um.tesoreria.core.hexagonal.pagos.entrega.infrastructure.persistence.entity.EntregaEntity;

@Component
public class EntregaMapper {

    public EntregaEntity toEntity(Entrega domain) {
        if (domain == null) {
            return null;
        }
        return EntregaEntity.builder()
                .entregaId(domain.entregaId())
                .fecha(domain.fecha())
                .ubicacionId(domain.ubicacionId())
                .recibe(domain.recibe())
                .observacion(domain.observacion())
                .fechaContable(domain.fechaContable())
                .ordenContable(domain.ordenContable())
                .anulada(domain.anulada())
                .tipo(domain.tipo())
                .trackId(domain.trackId())
                .build();
    }

    public Entrega toDomain(EntregaEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Entrega(
                entity.getEntregaId(),
                entity.getFecha(),
                entity.getUbicacionId(),
                entity.getRecibe(),
                entity.getObservacion(),
                entity.getFechaContable(),
                entity.getOrdenContable(),
                entity.getAnulada(),
                entity.getTipo(),
                entity.getTrackId()
        );
    }
}
