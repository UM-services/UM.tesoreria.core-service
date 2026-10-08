package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoAutorizanteResponse {

    private Integer autorizanteId;
    private List<Integer> dependenciaIds;

}
