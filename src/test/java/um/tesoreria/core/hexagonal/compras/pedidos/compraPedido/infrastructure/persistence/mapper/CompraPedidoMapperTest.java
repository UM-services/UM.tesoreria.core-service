package um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedido;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.domain.model.CompraPedidoEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraPedido.infrastructure.persistence.entity.CompraPedidoEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompraPedidoMapperTest {

    private final CompraPedidoMapper mapper = new CompraPedidoMapper();

    @Test
    void roundTripPreservaCabeceraYEstado() {
        CompraPedido domain = CompraPedido.builder()
                .compraPedidoId(1)
                .numero("PC-2026-000001")
                .ejercicioId(7)
                .fecha(LocalDateTime.of(2026, 10, 6, 9, 0))
                .estado(CompraPedidoEstado.PENDIENTE_ENVIO)
                .autorizanteId(99)
                .solicitanteId(10)
                .dependenciaId(20)
                .facultadId(30)
                .geograficaId(40)
                .necesidad("Renovación de equipamiento")
                .urgente(true)
                .montoConocido(true)
                .montoEstimado(new BigDecimal("4500000.00"))
                .fechaEnvio(LocalDateTime.of(2026, 10, 7, 12, 0))
                .rechazoMotivo("motivo")
                .descartadoMotivo("descarte")
                .build();

        CompraPedidoEntity entity = mapper.toEntity(domain);
        CompraPedido back = mapper.toDomain(entity);

        assertThat(entity.getEstado()).isEqualTo("PENDIENTE_ENVIO");
        assertThat(back.getEstado()).isEqualTo(CompraPedidoEstado.PENDIENTE_ENVIO);
        assertThat(back.getNumero()).isEqualTo("PC-2026-000001");
        assertThat(back.getEjercicioId()).isEqualTo(7);
        assertThat(back.getAutorizanteId()).isEqualTo(99);
        assertThat(back.getSolicitanteId()).isEqualTo(10);
        assertThat(back.getDependenciaId()).isEqualTo(20);
        assertThat(back.getFacultadId()).isEqualTo(30);
        assertThat(back.getGeograficaId()).isEqualTo(40);
        assertThat(back.getUrgente()).isTrue();
        assertThat(back.getMontoEstimado()).isEqualByComparingTo("4500000.00");
        assertThat(back.getFechaEnvio()).isEqualTo(LocalDateTime.of(2026, 10, 7, 12, 0));
        assertThat(back.getRechazoMotivo()).isEqualTo("motivo");
        assertThat(back.getDescartadoMotivo()).isEqualTo("descarte");
    }

    @Test
    void losFlagsNulosConservanElDefaultDeLaEntidad() {
        CompraPedido domain = CompraPedido.builder()
                .solicitanteId(1)
                .dependenciaId(2)
                .estado(CompraPedidoEstado.BORRADOR)
                .build();

        CompraPedidoEntity entity = mapper.toEntity(domain);

        assertThat(entity.getUrgente()).isFalse();
        assertThat(entity.getMontoConocido()).isFalse();
    }

    @Test
    void nullDevuelveNull() {
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toDomain(null)).isNull();
    }

}
