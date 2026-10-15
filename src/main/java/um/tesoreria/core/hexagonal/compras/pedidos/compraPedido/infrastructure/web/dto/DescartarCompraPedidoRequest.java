package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DescartarCompraPedidoRequest {

    private Integer usuarioId;
    private String motivo;

}
