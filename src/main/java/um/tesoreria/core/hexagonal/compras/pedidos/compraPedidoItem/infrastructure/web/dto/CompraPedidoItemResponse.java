package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoItem.infrastructure.web.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoItemResponse {

    private Integer compraPedidoItemId;
    private Integer compraPedidoId;
    private Integer orden;
    private BigDecimal cantidad;
    private String unidad;
    private String descripcion;
    private String especificaciones;
    private String referenciaWeb;

}
