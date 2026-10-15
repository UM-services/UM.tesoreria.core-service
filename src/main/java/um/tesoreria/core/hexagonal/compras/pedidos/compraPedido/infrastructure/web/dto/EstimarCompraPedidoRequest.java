package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EstimarCompraPedidoRequest {

    private BigDecimal montoEstimado;
    private String fuenteEstimacion;
    private Integer usuarioId;

}
