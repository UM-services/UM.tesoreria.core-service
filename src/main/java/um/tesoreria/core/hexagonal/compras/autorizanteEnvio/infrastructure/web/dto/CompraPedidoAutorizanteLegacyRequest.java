package um.tesoreria.core.hexagonal.compras.autorizanteEnvio.infrastructure.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraPedidoAutorizanteLegacyRequest {

    private Integer autorizanteId;
    private Integer dependenciaId;

}
