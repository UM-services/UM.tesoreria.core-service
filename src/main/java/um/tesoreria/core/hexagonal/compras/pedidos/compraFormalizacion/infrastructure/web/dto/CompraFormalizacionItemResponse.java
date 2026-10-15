package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraFormalizacionItemResponse {

    private Long compraFormalizacionItemId;
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
