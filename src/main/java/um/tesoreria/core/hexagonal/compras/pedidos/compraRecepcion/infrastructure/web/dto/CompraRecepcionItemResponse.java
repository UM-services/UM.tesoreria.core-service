package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto;

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
public class CompraRecepcionItemResponse {

    private Long compraRecepcionItemId;
    private Long compraFormalizacionItemId;
    private Integer orden;
    private BigDecimal cantidadRecibida;
    private String resultado;
    private String observacion;

}
