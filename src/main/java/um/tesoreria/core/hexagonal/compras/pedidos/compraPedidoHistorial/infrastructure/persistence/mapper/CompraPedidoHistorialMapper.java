package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.entity.CompraPedidoHistorialEntity;

@Component
public class CompraPedidoHistorialMapper {

    public CompraPedidoHistorialEntity toEntity(CompraPedidoHistorial domain) {
        if (domain == null) return null;
        return CompraPedidoHistorialEntity.builder()
                .compraPedidoHistorialId(domain.compraPedidoHistorialId())
                .compraPedidoId(domain.compraPedidoId())
                .estado(domain.estado())
                .usuarioId(domain.usuarioId())
                .observacion(domain.observacion())
                .fecha(domain.fecha())
                .build();
    }

    public CompraPedidoHistorial toDomain(CompraPedidoHistorialEntity entity) {
        if (entity == null) return null;
        return new CompraPedidoHistorial(
                entity.getCompraPedidoHistorialId(),
                entity.getCompraPedidoId(),
                entity.getEstado(),
                entity.getUsuarioId(),
                entity.getObservacion(),
                entity.getFecha());
    }

}
