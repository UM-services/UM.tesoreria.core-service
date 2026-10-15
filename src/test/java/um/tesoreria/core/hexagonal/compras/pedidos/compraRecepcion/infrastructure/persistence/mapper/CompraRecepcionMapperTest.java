package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPatrimonio;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionEntity;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.persistence.entity.CompraRecepcionItemEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class CompraRecepcionMapperTest {

    private final CompraRecepcionMapper mapper = new CompraRecepcionMapper();

    private CompraRecepcion domain() {
        return CompraRecepcion.builder()
                .compraRecepcionId(1L)
                .compraFormalizacionId(50L)
                .numero("RC-2026-000001")
                .ejercicioId(15)
                .fecha(LocalDateTime.of(2026, 10, 14, 10, 0))
                .remito("R-0001-004582")
                .resultado(CompraRecepcionResultado.CON_OBSERVACIONES)
                .patrimonio(CompraRecepcionPatrimonio.INTERVENIDO)
                .observacion("obs")
                .usuarioId(38)
                .build();
    }

    @Test
    void cabeceraIdaYVuelta() {
        CompraRecepcionEntity entity = mapper.toEntity(domain());
        assertThat(entity.getNumero()).isEqualTo("RC-2026-000001");
        assertThat(entity.getResultado()).isEqualTo("CON_OBSERVACIONES");
        assertThat(entity.getPatrimonio()).isEqualTo("INTERVENIDO");

        CompraRecepcion vuelta = mapper.toDomain(entity);
        assertThat(vuelta.getCompraFormalizacionId()).isEqualTo(50L);
        assertThat(vuelta.getResultado()).isEqualTo(CompraRecepcionResultado.CON_OBSERVACIONES);
        assertThat(vuelta.getPatrimonio()).isEqualTo(CompraRecepcionPatrimonio.INTERVENIDO);
    }

    @Test
    void updateEntityAplicaLosCampos() {
        CompraRecepcionEntity entity = CompraRecepcionEntity.builder().build();
        mapper.updateEntity(domain(), entity);

        assertThat(entity.getNumero()).isEqualTo("RC-2026-000001");
        assertThat(entity.getRemito()).isEqualTo("R-0001-004582");
        assertThat(entity.getUsuarioId()).isEqualTo(38);
    }

    @Test
    void itemIdaYVuelta() {
        CompraRecepcionItem item = CompraRecepcionItem.builder()
                .compraRecepcionItemId(2L)
                .compraRecepcionId(1L)
                .compraFormalizacionItemId(100L)
                .orden(1)
                .cantidadRecibida(new BigDecimal("2.00"))
                .resultado(CompraRecepcionResultado.NO_CONFORME)
                .observacion("faltante")
                .build();

        CompraRecepcionItemEntity entity = mapper.itemToEntity(item);
        CompraRecepcionItem vuelta = mapper.itemToDomain(entity);

        assertThat(vuelta.getCompraFormalizacionItemId()).isEqualTo(100L);
        assertThat(vuelta.getCantidadRecibida()).isEqualByComparingTo("2.00");
        assertThat(vuelta.getResultado()).isEqualTo(CompraRecepcionResultado.NO_CONFORME);
        assertThat(vuelta.getObservacion()).isEqualTo("faltante");
    }

    @Test
    void nulosDevuelvenNulo() {
        assertThat(mapper.toEntity(null)).isNull();
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.itemToEntity(null)).isNull();
        assertThat(mapper.itemToDomain(null)).isNull();
    }

    @Test
    void nulosSePropaganSinRomper() {
        CompraRecepcionEntity entity = mapper.toEntity(CompraRecepcion.builder().build());
        assertThat(entity.getResultado()).isNull();
        assertThat(entity.getPatrimonio()).isNull();
        assertThat(mapper.toDomain(CompraRecepcionEntity.builder().build()).getResultado()).isNull();
        assertThat(mapper.itemToDomain(CompraRecepcionItemEntity.builder().build()).getResultado()).isNull();
        mapper.updateEntity(null, entity);
        mapper.updateEntity(CompraRecepcion.builder().build(), null);
    }

}
