package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

/** Ítem contratado de la OC con su saldo derivado (bandeja / consulta de recepción). */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompraRecepcionPendienteItemResponse {

    private Long compraFormalizacionItemId;
    private Integer compraPedidoItemId;
    private Integer orden;
    private BigDecimal cantidad;
    private BigDecimal recibido;
    private BigDecimal saldo;
    private String unidad;
    private String descripcion;
    private String especificaciones;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    private String marca;
    private Boolean completo;

}
