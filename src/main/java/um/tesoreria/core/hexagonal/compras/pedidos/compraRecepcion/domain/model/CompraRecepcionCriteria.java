package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model;

import java.time.LocalDateTime;

/** Filtros de consulta de recepciones (todos opcionales). */
public record CompraRecepcionCriteria(
        Integer ejercicioId,
        Long compraFormalizacionId,
        CompraRecepcionResultado resultado,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {
}
