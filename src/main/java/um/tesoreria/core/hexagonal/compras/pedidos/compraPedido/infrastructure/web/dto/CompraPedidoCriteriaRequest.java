package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

import lombok.*;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Filtros de búsqueda de pedidos enviados en el cuerpo (mismo criterio que
 * {@code articulo/search}): evita parámetros opcionales en la query.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoCriteriaRequest {

    private CompraPedidoEstado estado;
    private Integer solicitanteId;
    private Integer dependenciaId;
    private List<Integer> dependenciaIds;
    private Integer ejercicioId;
    private LocalDateTime fechaDesde;
    private LocalDateTime fechaHasta;

}
