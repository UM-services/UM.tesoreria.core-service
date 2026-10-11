package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Ítem combinado de un proceso de cotización. Conserva el vínculo al ítem del pedido de origen
 * ({@code compraPedidoItemId}) para la trazabilidad.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraCotizacionItem {

    private Long compraCotizacionItemId;
    private Long compraCotizacionId;
    private Integer compraPedidoItemId;
    private Integer orden;
    private BigDecimal cantidad;
}
