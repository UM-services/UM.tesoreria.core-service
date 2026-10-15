package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPatrimonio;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendienteItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionItemRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionItemResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionPendienteItemResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionResponse;

import java.util.List;

@Component
public class CompraRecepcionDtoMapper {

    /** Datos editables del alta: número, ejercicio y resultado general los resuelve el servicio. */
    public CompraRecepcion toDomain(CompraRecepcionRequest request) {
        if (request == null) return null;
        return CompraRecepcion.builder()
                .compraFormalizacionId(request.getCompraFormalizacionId())
                .fecha(request.getFecha())
                .remito(request.getRemito())
                .patrimonio(patrimonio(request.getPatrimonio()))
                .observacion(request.getObservacion())
                .usuarioId(request.getUsuarioId())
                .items(request.getItems() == null ? List.of()
                        : request.getItems().stream().map(this::toDomain).toList())
                .build();
    }

    public CompraRecepcionItem toDomain(CompraRecepcionItemRequest request) {
        if (request == null) return null;
        return CompraRecepcionItem.builder()
                .compraFormalizacionItemId(request.getCompraFormalizacionItemId())
                .orden(request.getOrden())
                .cantidadRecibida(request.getCantidadRecibida())
                .resultado(resultado(request.getResultado()))
                .observacion(request.getObservacion())
                .build();
    }

    public CompraRecepcionResponse toResponse(CompraRecepcion domain) {
        if (domain == null) return null;
        return CompraRecepcionResponse.builder()
                .compraRecepcionId(domain.getCompraRecepcionId())
                .compraFormalizacionId(domain.getCompraFormalizacionId())
                .numero(domain.getNumero())
                .ejercicioId(domain.getEjercicioId())
                .fecha(domain.getFecha())
                .remito(domain.getRemito())
                .resultado(domain.getResultado() != null ? domain.getResultado().name() : null)
                .patrimonio(domain.getPatrimonio() != null ? domain.getPatrimonio().name() : null)
                .observacion(domain.getObservacion())
                .usuarioId(domain.getUsuarioId())
                .items(domain.getItems() == null ? List.of()
                        : domain.getItems().stream().map(this::toResponse).toList())
                .created(domain.getCreated())
                .updated(domain.getUpdated())
                .build();
    }

    public CompraRecepcionItemResponse toResponse(CompraRecepcionItem domain) {
        if (domain == null) return null;
        return CompraRecepcionItemResponse.builder()
                .compraRecepcionItemId(domain.getCompraRecepcionItemId())
                .compraFormalizacionItemId(domain.getCompraFormalizacionItemId())
                .orden(domain.getOrden())
                .cantidadRecibida(domain.getCantidadRecibida())
                .resultado(domain.getResultado() != null ? domain.getResultado().name() : null)
                .observacion(domain.getObservacion())
                .build();
    }

    public CompraRecepcionPendienteResponse toResponse(CompraRecepcionPendiente domain) {
        if (domain == null) return null;
        return CompraRecepcionPendienteResponse.builder()
                .compraFormalizacionId(domain.compraFormalizacionId())
                .numero(domain.numero())
                .numeroExterno(domain.numeroExterno())
                .ejercicioId(domain.ejercicioId())
                .proveedorId(domain.proveedorId())
                .fecha(domain.fecha())
                .monto(domain.monto())
                .moneda(domain.moneda())
                .items(domain.items() == null ? List.of()
                        : domain.items().stream().map(this::toResponse).toList())
                .recepciones(domain.recepciones() == null ? List.of()
                        : domain.recepciones().stream().map(this::toResponse).toList())
                .completo(domain.completo())
                .build();
    }

    public CompraRecepcionPendienteItemResponse toResponse(CompraRecepcionPendienteItem domain) {
        if (domain == null) return null;
        return CompraRecepcionPendienteItemResponse.builder()
                .compraFormalizacionItemId(domain.compraFormalizacionItemId())
                .compraPedidoItemId(domain.compraPedidoItemId())
                .orden(domain.orden())
                .cantidad(domain.cantidad())
                .recibido(domain.recibido())
                .saldo(domain.saldo())
                .unidad(domain.unidad())
                .descripcion(domain.descripcion())
                .especificaciones(domain.especificaciones())
                .precioUnitario(domain.precioUnitario())
                .subtotal(domain.subtotal())
                .marca(domain.marca())
                .completo(domain.completo())
                .build();
    }

    private static CompraRecepcionResultado resultado(String valor) {
        return valor == null || valor.isBlank() ? null : CompraRecepcionResultado.valueOf(valor.trim().toUpperCase());
    }

    private static CompraRecepcionPatrimonio patrimonio(String valor) {
        return valor == null || valor.isBlank() ? null : CompraRecepcionPatrimonio.valueOf(valor.trim().toUpperCase());
    }

}
