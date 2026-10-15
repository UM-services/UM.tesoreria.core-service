package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model.CompraPedidoItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.persistence.entity.CompraPedidoItemEntity;

@Component
public class CompraPedidoItemMapper {

    public CompraPedidoItemEntity toEntity(CompraPedidoItem domain) {
        if (domain == null) return null;
        return CompraPedidoItemEntity.builder()
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

    public CompraPedidoItem toDomain(CompraPedidoItemEntity entity) {
        if (entity == null) return null;
        return CompraPedidoItem.builder()
                .compraPedidoItemId(entity.getCompraPedidoItemId())
                .compraPedidoId(entity.getCompraPedidoId())
                .orden(entity.getOrden())
                .cantidad(entity.getCantidad())
                .unidad(entity.getUnidad())
                .descripcion(entity.getDescripcion())
                .especificaciones(entity.getEspecificaciones())
                .referenciaWeb(entity.getReferenciaWeb())
                .build();
    }

}
