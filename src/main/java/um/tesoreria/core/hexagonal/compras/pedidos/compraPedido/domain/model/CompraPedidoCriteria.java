package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Filtros opcionales de listado de pedidos. Todos los campos pueden ser {@code null}.
 *
 * <p>{@code dependenciaIds} permite acotar a las dependencias habilitadas de un autorizante
 * (bandeja de envío). Si viene vacío o {@code null}, no filtra por dependencia.</p>
 */
public record CompraPedidoCriteria(
        CompraPedidoEstado estado,
        Integer solicitanteId,
        Integer dependenciaId,
        Integer ejercicioId,
        LocalDateTime fechaDesde,
        LocalDateTime fechaHasta,
        List<Integer> dependenciaIds) {

}
