package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

import java.time.LocalDateTime;

/**
 * Filtros opcionales de listado de pedidos. Todos los campos pueden ser {@code null}.
 */
public record CompraPedidoCriteria(
        CompraPedidoEstado estado,
        Integer solicitanteId,
        Integer dependenciaId,
        Integer ejercicioId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta) {

}
