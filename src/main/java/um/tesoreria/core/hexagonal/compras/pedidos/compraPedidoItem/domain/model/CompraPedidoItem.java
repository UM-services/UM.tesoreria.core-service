package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.domain.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * Ítem de un pedido de compra. La unidad y el bien/servicio son texto libre: no se codifican
 * contra el maestro de artículos.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraPedidoItem {

    private Integer compraPedidoItemId;
    private Integer compraPedidoId;
    private Integer orden;
    private BigDecimal cantidad;
    private String unidad;
    private String descripcion;
    private String especificaciones;
    private String referenciaWeb;

}
