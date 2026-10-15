package um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPatrimonio;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionPendienteItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.domain.model.CompraRecepcionResultado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionItemRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraRecepcion.infrastructure.web.dto.CompraRecepcionResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CompraRecepcionDtoMapperTest {

    private final CompraRecepcionDtoMapper mapper = new CompraRecepcionDtoMapper();

    @Test
    void toDomainMapeaLaCabeceraYElDetalle() {
        CompraRecepcionRequest request = CompraRecepcionRequest.builder()
                .compraFormalizacionId(50L)
                .remito("R-1")
                .patrimonio("intervenido")
                .observacion("obs")
                .usuarioId(38)
                .items(List.of(CompraRecepcionItemRequest.builder()
                        .compraFormalizacionItemId(100L)
                        .orden(1)
                        .cantidadRecibida(new BigDecimal("2.00"))
                        .resultado("con_observaciones")
                        .observacion("detalle")
                        .build()))
                .build();

        CompraRecepcion domain = mapper.toDomain(request);

        assertThat(domain.getCompraFormalizacionId()).isEqualTo(50L);
        assertThat(domain.getPatrimonio()).isEqualTo(CompraRecepcionPatrimonio.INTERVENIDO);
        assertThat(domain.getItems()).hasSize(1);
        assertThat(domain.getItems().get(0).getResultado()).isEqualTo(CompraRecepcionResultado.CON_OBSERVACIONES);
        assertThat(domain.getItems().get(0).getObservacion()).isEqualTo("detalle");
    }

    @Test
    void toDomainDejaNulosLosCamposOpcionalesVacios() {
        CompraRecepcion domain = mapper.toDomain(CompraRecepcionRequest.builder()
                .compraFormalizacionId(50L).patrimonio("  ").build());

        assertThat(domain.getPatrimonio()).isNull();
        assertThat(domain.getItems()).isEmpty();
    }

    @Test
    void toDomainFallaConUnResultadoDesconocido() {
        CompraRecepcionItemRequest item = CompraRecepcionItemRequest.builder()
                .resultado("DESCONOCIDO").build();

        assertThatThrownBy(() -> mapper.toDomain(item)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void toResponseMapeaLaRecepcionConSuDetalle() {
        CompraRecepcion domain = CompraRecepcion.builder()
                .compraRecepcionId(1L)
                .compraFormalizacionId(50L)
                .numero("RC-2026-000001")
                .resultado(CompraRecepcionResultado.NO_CONFORME)
                .patrimonio(CompraRecepcionPatrimonio.PENDIENTE)
                .items(List.of(CompraRecepcionItem.builder().compraRecepcionItemId(2L)
                        .compraFormalizacionItemId(100L).orden(1).cantidadRecibida(new BigDecimal("1.00"))
                        .resultado(CompraRecepcionResultado.NO_CONFORME).build()))
                .build();

        CompraRecepcionResponse response = mapper.toResponse(domain);

        assertThat(response.getNumero()).isEqualTo("RC-2026-000001");
        assertThat(response.getResultado()).isEqualTo("NO_CONFORME");
        assertThat(response.getPatrimonio()).isEqualTo("PENDIENTE");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getResultado()).isEqualTo("NO_CONFORME");
    }

    @Test
    void toResponseConNulosNoRompe() {
        CompraRecepcion sinDatos = CompraRecepcion.builder().build();
        CompraRecepcionResponse response = mapper.toResponse(sinDatos);

        assertThat(response.getResultado()).isNull();
        assertThat(response.getItems()).isEmpty();
    }

    @Test
    void toResponseMapeaLaPendienteConSaldos() {
        CompraRecepcionPendiente pendiente = new CompraRecepcionPendiente(50L, "OC-2026-000003", "OC-EXT", 15, 7,
                null, new BigDecimal("28000.00"), "ARS",
                List.of(new CompraRecepcionPendienteItem(100L, 1000, 1, new BigDecimal("3.00"),
                        new BigDecimal("1.00"), new BigDecimal("2.00"), "Unidad", "Notebook", "<p>x</p>",
                        new BigDecimal("9000.00"), new BigDecimal("27000.00"), "ACME")),
                List.of(CompraRecepcion.builder().compraRecepcionId(1L).build()));

        CompraRecepcionPendienteResponse response = mapper.toResponse(pendiente);

        assertThat(response.getNumero()).isEqualTo("OC-2026-000003");
        assertThat(response.getCompleto()).isFalse();
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getSaldo()).isEqualByComparingTo("2.00");
        assertThat(response.getItems().get(0).getCompleto()).isFalse();
        assertThat(response.getRecepciones()).hasSize(1);
    }

    @Test
    void nulosDevuelvenNulo() {
        assertThat(mapper.toDomain((CompraRecepcionRequest) null)).isNull();
        assertThat(mapper.toDomain((CompraRecepcionItemRequest) null)).isNull();
        assertThat(mapper.toResponse((CompraRecepcion) null)).isNull();
        assertThat(mapper.toResponse((CompraRecepcionItem) null)).isNull();
        assertThat(mapper.toResponse((CompraRecepcionPendiente) null)).isNull();
        assertThat(mapper.toResponse((CompraRecepcionPendienteItem) null)).isNull();
    }

}
