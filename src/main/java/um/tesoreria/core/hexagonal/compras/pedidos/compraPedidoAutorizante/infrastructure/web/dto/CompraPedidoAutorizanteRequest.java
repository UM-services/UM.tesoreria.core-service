package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoAutorizanteRequest {

    private Integer autorizanteId;
    private Integer dependenciaId;

}
