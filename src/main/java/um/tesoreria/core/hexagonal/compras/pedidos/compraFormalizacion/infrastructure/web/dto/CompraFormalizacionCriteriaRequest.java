package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto;

import java.time.LocalDateTime;

/** Filtros de consulta de Órdenes de Compra (todos opcionales). */
public record CompraFormalizacionCriteriaRequest(
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
