package um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.domain.model.CompraFormalizacionPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionPendienteResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraFormalizacion.infrastructure.web.dto.CompraFormalizacionResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompraFormalizacionDtoMapperTest {

    private final CompraFormalizacionDtoMapper mapper = new CompraFormalizacionDtoMapper();

    @Test
    void toDomainSoloTomaLosCamposEditables() {
        CompraFormalizacionRequest request = CompraFormalizacionRequest.builder()
                .compraCotizacionId(1L)
                .numeroExterno("OC-EXT")
                .moneda("ARS")
                .monto(new BigDecimal("28000.00"))
                .condicionPago("30 días")
                .objeto("<p>Objeto</p>")
                .usuarioId(38)
                .build();

        CompraFormalizacion domain = mapper.toDomain(request);

        assertThat(domain.getNumeroExterno()).isEqualTo("OC-EXT");
        assertThat(domain.getMonto()).isEqualByComparingTo("28000.00");
        assertThat(domain.getObjeto()).isEqualTo("<p>Objeto</p>");
        assertThat(domain.getNumero()).isNull();
    }

    @Test
    void toResponseIncluyeLosItems() {
        CompraFormalizacion domain = CompraFormalizacion.builder()
                .compraFormalizacionId(1L)
                .numero("OC-2026-000001")
                .proveedorId(7)
                .items(List.of(CompraFormalizacionItem.builder().orden(1).descripcion("Notebook").build()))
                .build();

        CompraFormalizacionResponse response = mapper.toResponse(domain);

        assertThat(response.getNumero()).isEqualTo("OC-2026-000001");
        assertThat(response.getItems()).hasSize(1);
        assertThat(response.getItems().get(0).getDescripcion()).isEqualTo("Notebook");
    }

    @Test
    void toResponsePendiente() {
        CompraFormalizacionPendiente pendiente = new CompraFormalizacionPendiente(1L, "SC-2026-000001", 15,
                9L, 5L, 7, new BigDecimal("28000.00"), "ARS", "30 días", "48 horas", "12 meses",
                List.of(CompraFormalizacionItem.builder().orden(1).descripcion("Notebook").build()));

        CompraFormalizacionPendienteResponse response = mapper.toResponse(pendiente);

        assertThat(response.getCompraCotizacionId()).isEqualTo(1L);
        assertThat(response.getCompraAdjudicacionId()).isEqualTo(9L);
        assertThat(response.getProveedorId()).isEqualTo(7);
        assertThat(response.getItems()).hasSize(1);
    }

    @Test
    void nulosDevuelvenNulo() {
        assertThat(mapper.toDomain(null)).isNull();
        assertThat(mapper.toResponse((CompraFormalizacion) null)).isNull();
        assertThat(mapper.toResponse((CompraFormalizacionItem) null)).isNull();
        assertThat(mapper.toResponse((CompraFormalizacionPendiente) null)).isNull();
    }
}
