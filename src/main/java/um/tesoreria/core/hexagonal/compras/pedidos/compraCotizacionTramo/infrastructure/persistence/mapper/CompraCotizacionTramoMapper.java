package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.persistence.entity.CompraCotizacionTramoEntity;

@Component
public class CompraCotizacionTramoMapper {

    public CompraCotizacionTramo toDomain(CompraCotizacionTramoEntity entity) {
        if (entity == null) return null;
        CompraCotizacionTramo.CompraCotizacionTramoBuilder builder = CompraCotizacionTramo.builder()
                .cotizacionTramoId(entity.getCotizacionTramoId())
                .nombre(entity.getNombre())
                // topeMultiplico es intencionalmente nullable (nulo = tramo sin tope): se copia tal cual.
                .topeMultiplico(entity.getTopeMultiplico())
                .cantidadMinima(entity.getCantidadMinima())
                .created(entity.getCreated())
                .updated(entity.getUpdated());
        if (entity.getActivo() != null) builder.activo(entity.getActivo());
        return builder.build();
    }

    public CompraCotizacionTramoEntity toEntity(CompraCotizacionTramo domain) {
        if (domain == null) return null;
        CompraCotizacionTramoEntity.CompraCotizacionTramoEntityBuilder builder = CompraCotizacionTramoEntity.builder()
                .cotizacionTramoId(domain.getCotizacionTramoId())
                .nombre(domain.getNombre())
                .topeMultiplico(domain.getTopeMultiplico())
                .cantidadMinima(domain.getCantidadMinima());
        if (domain.getActivo() != null) builder.activo(domain.getActivo());
        return builder.build();
    }

    /**
     * Aplica los campos del dominio sobre una entidad YA MANAGED. {@code topeMultiplico} se setea
     * siempre (permite limpiarlo a nulo = sin tope); el resto de nulls se ignoran.
     */
    public void updateEntity(CompraCotizacionTramo domain, CompraCotizacionTramoEntity entity) {
        if (domain == null || entity == null) return;
        entity.setNombre(domain.getNombre());
        entity.setTopeMultiplico(domain.getTopeMultiplico());
        if (domain.getCantidadMinima() != null) entity.setCantidadMinima(domain.getCantidadMinima());
        if (domain.getActivo() != null) entity.setActivo(domain.getActivo());
    }

}
