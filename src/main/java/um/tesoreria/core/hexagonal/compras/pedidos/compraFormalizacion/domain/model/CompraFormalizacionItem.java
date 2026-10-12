package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Ítem de la Orden de Compra (snapshot): copia la cantidad y el vínculo al ítem del proceso
 * ({@code compraCotizacionItemId}) y al ítem del pedido de origen ({@code compraPedidoItemId}),
 * la descripción/unidad del pedido y el precio/marca de la oferta adjudicada.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFormalizacionItem {

    private Long compraFormalizacionItemId;
    private Long compraFormalizacionId;
    private Long compraCotizacionItemId;
    private Integer compraPedidoItemId;
    private Integer orden;
    private BigDecimal cantidad;
    private String unidad;
    private String descripcion;
    private String especificaciones;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String marca;

}
