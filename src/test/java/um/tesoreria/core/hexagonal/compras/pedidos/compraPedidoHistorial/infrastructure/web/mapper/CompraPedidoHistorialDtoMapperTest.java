package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.web.dto.CompraPedidoHistorialResponse;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompraPedidoHistorialDtoMapperTest {

    private final CompraPedidoHistorialDtoMapper mapper = new CompraPedidoHistorialDtoMapper();

    @Test
    void toResponseCopiaLosCampos() {
        CompraPedidoHistorial domain = new CompraPedidoHistorial(1L, 5, "ENVIADO", 9, null,
                LocalDateTime.of(2026, 10, 8, 10, 0));

        CompraPedidoHistorialResponse response = mapper.toResponse(domain);

        assertThat(response.getCompraPedidoHistorialId()).isEqualTo(1L);
        assertThat(response.getCompraPedidoId()).isEqualTo(5);
        assertThat(response.getEstado()).isEqualTo("ENVIADO");
        assertThat(response.getUsuarioId()).isEqualTo(9);
    }

    @Test
    void nullDevuelveNull() {
        assertThat(mapper.toResponse(null)).isNull();
    }

}
