package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.domain.model.CompraCotizacionTramo;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionTramoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacionTramo.infrastructure.web.dto.CompraCotizacionTramoResponse;

@Component
public class CompraCotizacionTramoDtoMapper {

    public CompraCotizacionTramo toDomain(CompraCotizacionTramoRequest request) {
        if (request == null) return null;
        CompraCotizacionTramo.CompraCotizacionTramoBuilder builder = CompraCotizacionTramo.builder()
                .nombre(request.getNombre())
                .topeMultiplico(request.getTopeMultiplico())
                .cantidadMinima(request.getCantidadMinima());
        if (request.getActivo() != null) builder.activo(request.getActivo());
        return builder.build();
    }

    public CompraCotizacionTramoResponse toResponse(CompraCotizacionTramo domain) {
        if (domain == null) return null;
        return CompraCotizacionTramoResponse.builder()
                .cotizacionTramoId(domain.getCotizacionTramoId())
                .nombre(domain.getNombre())
                .topeMultiplico(domain.getTopeMultiplico())
                .cantidadMinima(domain.getCantidadMinima())
                .abierto(domain.esAbierto())
                .activo(domain.getActivo())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }

}
