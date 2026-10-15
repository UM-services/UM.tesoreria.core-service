package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AprobarCompraPedidoRequest {

    private Integer autorizanteId;

}
