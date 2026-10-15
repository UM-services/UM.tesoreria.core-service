package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.dto.CompraPedidoHistorialResponse;

@Component
public class CompraPedidoHistorialDtoMapper {

    public CompraPedidoHistorialResponse toResponse(CompraPedidoHistorial domain) {
        if (domain == null) return null;
        return CompraPedidoHistorialResponse.builder()
                .compraPedidoHistorialId(domain.compraPedidoHistorialId())
                .compraPedidoId(domain.compraPedidoId())
                .estado(domain.estado())
                .usuarioId(domain.usuarioId())
                .observacion(domain.observacion())
                .fecha(domain.fecha())
                .build();
    }

}
