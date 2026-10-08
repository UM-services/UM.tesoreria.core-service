package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.mapper;

import org.springframework.stereotype.Component;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.dto.CompraPedidoAutorizanteResponse;

import java.util.List;

@Component
public class CompraPedidoAutorizanteDtoMapper {

    public CompraPedidoAutorizanteResponse toResponse(Integer autorizanteId, List<Integer> dependenciaIds) {
        return CompraPedidoAutorizanteResponse.builder()
                .autorizanteId(autorizanteId)
                .dependenciaIds(dependenciaIds)
                .build();
    }

}
