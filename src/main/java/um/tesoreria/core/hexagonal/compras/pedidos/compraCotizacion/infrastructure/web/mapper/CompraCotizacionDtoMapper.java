package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionItemResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraAdjudicacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionProveedorItemResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionProveedorPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionProveedorResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionRespuestaRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionResponse;

import java.util.List;

@Component
public class CompraCotizacionDtoMapper {

    public CompraCotizacion toDomain(CompraCotizacionRequest request) {
        if (request == null) return null;
        CompraCotizacion proceso = CompraCotizacion.builder()
                .ejercicioId(request.ejercicioId())
                .fecha(request.fecha())
                .observacion(request.observacion())
                .fechaLimiteRespuesta(request.fechaLimiteRespuesta())
                .montoEstimado(request.montoEstimado())
                .cantidadMinima(request.cantidadMinima())
                .excepcionMotivo(request.excepcionMotivo())
                .usuarioId(request.usuarioId())
                .build();
        if (request.items() != null) {
            proceso.setItems(request.items().stream()
                    .map(i -> CompraCotizacionItem.builder()
                            .compraPedidoItemId(i.compraPedidoItemId())
                            .orden(i.orden())
                            .cantidad(i.cantidad())
                            .build())
                    .toList());
        }
        if (request.proveedores() != null) {
            proceso.setProveedores(request.proveedores().stream()
                    .map(p -> CompraCotizacionProveedor.builder()
                            .proveedorId(p.proveedorId())
                            .email(p.email())
                            .observacion(p.observacion())
                            .estado(CompraCotizacionProveedorEstado.PENDIENTE)
                            .build())
                    .toList());
        }
        return proceso;
    }

    /** Respuesta de un proveedor (cabecera + precios por ítem). */
    public CompraCotizacionProveedor toDomainRespuesta(CompraCotizacionRespuestaRequest request) {
        if (request == null) return null;
        CompraCotizacionProveedor proveedor = CompraCotizacionProveedor.builder()
                .fechaRespuesta(request.fechaRespuesta())
                .moneda(request.moneda())
                .total(request.total())
                .plazoEntrega(request.plazoEntrega())
                .condicionPago(request.condicionPago())
                .garantia(request.garantia())
                .validezOferta(request.validezOferta())
                .build();
        if (request.items() != null) {
            proveedor.setItems(request.items().stream()
                    .map(i -> CompraCotizacionProveedorItem.builder()
                            .compraCotizacionItemId(i.compraCotizacionItemId())
                            .cotiza(i.cotiza() == null ? Boolean.TRUE : i.cotiza())
                            .precioUnitario(i.precioUnitario())
                            .subtotal(i.subtotal())
                            .marca(i.marca())
                            .observacion(i.observacion())
                            .build())
                    .toList());
        }
        return proveedor;
    }

    public CompraCotizacionResponse toResponse(CompraCotizacion domain) {
        if (domain == null) return null;
        List<CompraCotizacionItemResponse> items = domain.getItems() == null ? List.of()
                : domain.getItems().stream()
                        .map(i -> new CompraCotizacionItemResponse(i.getCompraCotizacionItemId(),
                                i.getCompraPedidoItemId(), i.getOrden(), i.getCantidad()))
                        .toList();
        List<CompraCotizacionProveedorResponse> proveedores = domain.getProveedores() == null ? List.of()
                : domain.getProveedores().stream().map(this::toResponse).toList();
        return new CompraCotizacionResponse(domain.getCompraCotizacionId(), domain.getNumero(),
                domain.getEjercicioId(), domain.getFecha(),
                domain.getEstado() != null ? domain.getEstado().name() : null, domain.getObservacion(),
                domain.getFechaLimiteRespuesta(), domain.getMontoEstimado(), domain.getCantidadMinima(),
                domain.getExcepcionMotivo(), domain.getFechaEnvio(), domain.getProveedorRecomendadoId(),
                domain.getFundamentoRecomendacion(), domain.getFechaRecomendacion(), domain.getUsuarioId(),
                items, proveedores);
    }

    public CompraCotizacionProveedorResponse toResponse(CompraCotizacionProveedor proveedor) {
        if (proveedor == null) return null;
        List<CompraCotizacionProveedorItemResponse> items = proveedor.getItems() == null ? List.of()
                : proveedor.getItems().stream()
                        .map(i -> new CompraCotizacionProveedorItemResponse(i.getCompraCotizacionProveedorItemId(),
                                i.getCompraCotizacionItemId(), i.getCotiza(), i.getPrecioUnitario(), i.getSubtotal(),
                                i.getMarca(), i.getObservacion()))
                        .toList();
        return new CompraCotizacionProveedorResponse(proveedor.getCompraCotizacionProveedorId(),
                proveedor.getProveedorId(), proveedor.getEmail(),
                proveedor.getEstado() != null ? proveedor.getEstado().name() : null, proveedor.getFechaEnvio(),
                proveedor.getObservacion(), proveedor.getFechaRespuesta(), proveedor.getMoneda(),
                proveedor.getTotal(), proveedor.getPlazoEntrega(), proveedor.getCondicionPago(),
                proveedor.getGarantia(), proveedor.getValidezOferta(), items);
    }

    public CompraCotizacionPendienteResponse toResponse(CompraCotizacionPendiente pendiente) {
        if (pendiente == null) return null;
        return new CompraCotizacionPendienteResponse(pendiente.compraPedidoItemId(), pendiente.compraPedidoId(),
                pendiente.pedidoNumero(), pendiente.dependenciaId(), pendiente.orden(), pendiente.cantidad(),
                pendiente.unidad(), pendiente.descripcion(), pendiente.especificaciones(), pendiente.referenciaWeb());
    }

    /** Documento de proveedor pendiente de respuesta (bandeja transversal "Cotizaciones pedidas"). */
    public CompraCotizacionProveedorPendienteResponse toResponse(CompraCotizacionProveedorPendiente pendiente) {
        if (pendiente == null) return null;
        List<CompraCotizacionItemResponse> items = pendiente.items() == null ? List.of()
                : pendiente.items().stream()
                        .map(i -> new CompraCotizacionItemResponse(i.getCompraCotizacionItemId(),
                                i.getCompraPedidoItemId(), i.getOrden(), i.getCantidad()))
                        .toList();
        return new CompraCotizacionProveedorPendienteResponse(pendiente.compraCotizacionId(), pendiente.numero(),
                pendiente.ejercicioId(), pendiente.fechaLimiteRespuesta(), toResponse(pendiente.proveedor()), items);
    }

    /** Respuesta del acto de adjudicación (etapa 7). */
    public CompraAdjudicacionResponse toResponse(CompraAdjudicacion adjudicacion) {
        if (adjudicacion == null) return null;
        return new CompraAdjudicacionResponse(adjudicacion.getCompraAdjudicacionId(),
                adjudicacion.getCompraCotizacionId(), adjudicacion.getEjercicioId(),
                adjudicacion.getCompraCotizacionProveedorId(), adjudicacion.getProveedorAdjudicadoId(),
                adjudicacion.getMonto(),
                adjudicacion.getDecision() != null ? adjudicacion.getDecision().name() : null,
                adjudicacion.getFundamento(), adjudicacion.getAutoridadUsuarioId(),
                adjudicacion.getLimiteAplicado(), adjudicacion.getFecha());
    }
}
