package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.web.dto;

import lombok.*;

/**
 * Cuerpo opcional del envío de un pedido: el usuario que lo presenta.
 * Si no viene, el pedido se envía sin usuario asociado.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EnviarCompraPedidoRequest {

    private Integer usuarioId;

}
