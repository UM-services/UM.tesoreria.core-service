package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoAutorizanteLegacyResponse {

    private Integer autorizanteId;
    private List<Integer> dependenciaIds;

}
