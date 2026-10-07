package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.dto.CompraPedidoItemRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.dto.CompraPedidoItemResponse;

import java.util.List;

@Component
public class CompraPedidoItemDtoMapper {

    public CompraPedidoItem toDomain(CompraPedidoItemRequest request) {
        if (request == null) return null;
        return CompraPedidoItem.builder()
                .orden(request.getOrden())
                .cantidad(request.getCantidad())
                .unidad(request.getUnidad())
                .descripcion(request.getDescripcion())
                .especificaciones(request.getEspecificaciones())
                .referenciaWeb(request.getReferenciaWeb())
                .build();
    }

    public List<CompraPedidoItem> toDomain(List<CompraPedidoItemRequest> requests) {
        if (requests == null) return List.of();
        return requests.stream().map(this::toDomain).toList();
    }

    public CompraPedidoItemResponse toResponse(CompraPedidoItem domain) {
        if (domain == null) return null;
        return CompraPedidoItemResponse.builder()
                .compraPedidoItemId(domain.getCompraPedidoItemId())
                .compraPedidoId(domain.getCompraPedidoId())
                .orden(domain.getOrden())
                .cantidad(domain.getCantidad())
                .unidad(domain.getUnidad())
                .descripcion(domain.getDescripcion())
                .especificaciones(domain.getEspecificaciones())
                .referenciaWeb(domain.getReferenciaWeb())
                .build();
    }

}
