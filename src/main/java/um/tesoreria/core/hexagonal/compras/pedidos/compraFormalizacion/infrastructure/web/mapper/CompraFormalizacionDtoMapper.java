package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionItemResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionResponse;

import java.util.List;

@Component
public class CompraFormalizacionDtoMapper {

    /** Datos editables del alta: el proveedor y los ítems se snapshotean, no vienen en el request. */
    public CompraFormalizacion toDomain(CompraFormalizacionRequest request) {
        if (request == null) return null;
        return CompraFormalizacion.builder()
                .numeroExterno(request.getNumeroExterno())
                .moneda(request.getMoneda())
                .monto(request.getMonto())
                .condicionPago(request.getCondicionPago())
                .plazoEntrega(request.getPlazoEntrega())
                .garantia(request.getGarantia())
                .objeto(request.getObjeto())
                .observacion(request.getObservacion())
                .usuarioId(request.getUsuarioId())
                .build();
    }

    public CompraFormalizacionResponse toResponse(CompraFormalizacion domain) {
        if (domain == null) return null;
        return CompraFormalizacionResponse.builder()
                .compraFormalizacionId(domain.getCompraFormalizacionId())
                .compraAdjudicacionId(domain.getCompraAdjudicacionId())
                .numero(domain.getNumero())
                .numeroExterno(domain.getNumeroExterno())
                .ejercicioId(domain.getEjercicioId())
                .fecha(domain.getFecha())
                .proveedorId(domain.getProveedorId())
                .moneda(domain.getMoneda())
                .monto(domain.getMonto())
                .condicionPago(domain.getCondicionPago())
                .plazoEntrega(domain.getPlazoEntrega())
                .garantia(domain.getGarantia())
                .objeto(domain.getObjeto())
                .observacion(domain.getObservacion())
                .usuarioId(domain.getUsuarioId())
                .items(domain.getItems() == null ? List.of()
                        : domain.getItems().stream().map(this::toResponse).toList())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }

    public CompraFormalizacionItemResponse toResponse(CompraFormalizacionItem domain) {
        if (domain == null) return null;
        return CompraFormalizacionItemResponse.builder()
                .compraFormalizacionItemId(domain.getCompraFormalizacionItemId())
                .compraCotizacionItemId(domain.getCompraCotizacionItemId())
                .compraPedidoItemId(domain.getCompraPedidoItemId())
                .orden(domain.getOrden())
                .cantidad(domain.getCantidad())
                .unidad(domain.getUnidad())
                .descripcion(domain.getDescripcion())
                .especificaciones(domain.getEspecificaciones())
                .precioUnitario(domain.getPrecioUnitario())
                .subtotal(domain.getSubtotal())
                .marca(domain.getMarca())
                .build();
    }

    public CompraFormalizacionPendienteResponse toResponse(CompraFormalizacionPendiente domain) {
        if (domain == null) return null;
        return CompraFormalizacionPendienteResponse.builder()
                .compraCotizacionId(domain.compraCotizacionId())
                .numero(domain.numero())
                .ejercicioId(domain.ejercicioId())
                .compraAdjudicacionId(domain.compraAdjudicacionId())
                .compraCotizacionProveedorId(domain.compraCotizacionProveedorId())
                .proveedorId(domain.proveedorId())
                .monto(domain.monto())
                .moneda(domain.moneda())
                .condicionPago(domain.condicionPago())
                .plazoEntrega(domain.plazoEntrega())
                .garantia(domain.garantia())
                .items(domain.items() == null ? List.of()
                        : domain.items().stream().map(this::toResponse).toList())
                .build();
    }

}
