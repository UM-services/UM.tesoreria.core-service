package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import java.time.LocalDateTime;

/** Filtros de consulta de procesos de cotización (todos opcionales). */
public record CompraCotizacionCriteria(
        String estado,
        Integer ejercicioId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
