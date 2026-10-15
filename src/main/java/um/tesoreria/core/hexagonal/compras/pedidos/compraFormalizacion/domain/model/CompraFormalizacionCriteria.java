package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model;

import java.time.LocalDateTime;

/** Filtros de consulta de Órdenes de Compra (todos opcionales). */
public record CompraFormalizacionCriteria(
        Integer ejercicioId,
        Integer proveedorId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
