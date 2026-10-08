package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.CompraPedidoRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto.CompraPedidoResponse;

@Component
public class CompraPedidoDtoMapper {

    public CompraPedido toDomain(CompraPedidoRequest request) {
        if (request == null) return null;
        return CompraPedido.builder()
                .ejercicioId(request.getEjercicioId())
                .solicitanteId(request.getSolicitanteId())
                .dependenciaId(request.getDependenciaId())
                .facultadId(request.getFacultadId())
                .geograficaId(request.getGeograficaId())
                .necesidad(request.getNecesidad())
                .fechaRequerida(request.getFechaRequerida())
                .urgente(request.getUrgente())
                .urgenciaMotivo(request.getUrgenciaMotivo())
                .montoConocido(request.getMontoConocido())
                .montoEstimado(request.getMontoEstimado())
                .fuenteEstimacion(request.getFuenteEstimacion())
                .build();
    }

    public CompraPedidoResponse toResponse(CompraPedido domain) {
        if (domain == null) return null;
        return CompraPedidoResponse.builder()
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
                .urgente(domain.getUrgente())
                .urgenciaMotivo(domain.getUrgenciaMotivo())
                .montoConocido(domain.getMontoConocido())
                .montoEstimado(domain.getMontoEstimado())
                .fuenteEstimacion(domain.getFuenteEstimacion())
                .fechaEnvio(domain.getFechaEnvio())
                .rechazoMotivo(domain.getRechazoMotivo())
                .descartadoMotivo(domain.getDescartadoMotivo())
                .build();
    }

}
