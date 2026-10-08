package um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.domain.model.CompraPedidoHistorial;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedidoHistorial.infrastructure.persistence.entity.CompraPedidoHistorialEntity;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompraPedidoHistorialMapperTest {

    private final CompraPedidoHistorialMapper mapper = new CompraPedidoHistorialMapper();

    @Test
    void roundTripPreservaLosCampos() {
        CompraPedidoHistorial domain = new CompraPedidoHistorial(1L, 5, "RECHAZADO", 9, "Falta cotización",
                LocalDateTime.of(2026, 10, 8, 10, 0));

        CompraPedidoHistorialEntity entity = mapper.toEntity(domain);
        CompraPedidoHistorial back = mapper.toDomain(entity);

        assertThat(entity.getEstado()).isEqualTo("RECHAZADO");
        assertThat(back.compraPedidoHistorialId()).isEqualTo(1L);
        assertThat(back.compraPedidoId()).isEqualTo(5);
        assertThat(back.usuarioId()).isEqualTo(9);
        assertThat(back.observacion()).isEqualTo("Falta cotización");
        assertThat(back.fecha()).isEqualTo(LocalDateTime.of(2026, 10, 8, 10, 0));
    }

    @Test
    void nullDevuelveNull() {
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toDomain(null)).isNull();
    }

}
