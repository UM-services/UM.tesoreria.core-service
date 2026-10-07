package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.entity.CompraPedidoEntity;

@Component
public class CompraPedidoMapper {

    public CompraPedidoEntity toEntity(CompraPedido domain) {
        if (domain == null) return null;
        CompraPedidoEntity.CompraPedidoEntityBuilder builder = CompraPedidoEntity.builder()
                .compraPedidoId(domain.getCompraPedidoId())
                .numero(domain.getNumero())
                .ejercicioId(domain.getEjercicioId())
                .fecha(domain.getFecha())
                .estado(domain.getEstado() != null ? domain.getEstado().name() : null)
                .autorizanteId(domain.getAutorizanteId())
                .solicitanteId(domain.getSolicitanteId())
                .dependenciaId(domain.getDependenciaId())
                .facultadId(domain.getFacultadId())
                .geograficaId(domain.getGeograficaId())
                .necesidad(domain.getNecesidad())
                .fechaRequerida(domain.getFechaRequerida())
                .urgenciaMotivo(domain.getUrgenciaMotivo())
                .montoEstimado(domain.getMontoEstimado())
                .fuenteEstimacion(domain.getFuenteEstimacion());
        if (domain.getUrgente() != null) builder.urgente(domain.getUrgente());
        if (domain.getMontoConocido() != null) builder.montoConocido(domain.getMontoConocido());
        return builder.build();
    }

    public CompraPedido toDomain(CompraPedidoEntity entity) {
        if (entity == null) return null;
        return CompraPedido.builder()
                .compraPedidoId(entity.getCompraPedidoId())
                .numero(entity.getNumero())
                .ejercicioId(entity.getEjercicioId())
                .fecha(entity.getFecha())
                .estado(entity.getEstado() != null ? CompraPedidoEstado.valueOf(entity.getEstado()) : null)
                .autorizanteId(entity.getAutorizanteId())
                .solicitanteId(entity.getSolicitanteId())
                .dependenciaId(entity.getDependenciaId())
                .facultadId(entity.getFacultadId())
                .geograficaId(entity.getGeograficaId())
                .necesidad(entity.getNecesidad())
                .fechaRequerida(entity.getFechaRequerida())
                .urgente(entity.getUrgente())
                .urgenciaMotivo(entity.getUrgenciaMotivo())
                .montoConocido(entity.getMontoConocido())
                .montoEstimado(entity.getMontoEstimado())
                .fuenteEstimacion(entity.getFuenteEstimacion())
                .build();
    }

}
