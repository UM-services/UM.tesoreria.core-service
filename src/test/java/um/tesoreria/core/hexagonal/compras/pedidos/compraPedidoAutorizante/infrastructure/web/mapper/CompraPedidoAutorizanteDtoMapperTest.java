package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoAutorizante.infrastructure.web.dto.CompraPedidoAutorizanteResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompraPedidoAutorizanteDtoMapperTest {

    private final CompraPedidoAutorizanteDtoMapper mapper = new CompraPedidoAutorizanteDtoMapper();

    @Test
    void toResponseArmaElAutorizanteYSuLista() {
        CompraPedidoAutorizanteResponse response = mapper.toResponse(9, List.of(1, 2));

        assertThat(response.getAutorizanteId()).isEqualTo(9);
        assertThat(response.getDependenciaIds()).containsExactly(1, 2);
    }

}
