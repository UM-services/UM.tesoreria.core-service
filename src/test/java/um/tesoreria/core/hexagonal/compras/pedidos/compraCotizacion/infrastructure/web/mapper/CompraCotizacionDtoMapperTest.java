package um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.mapper;

import org.junit.jupiter.api.Test;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacion;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraAdjudicacionDecision;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionItem;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedor;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorEstado;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorPendiente;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraAdjudicacionResponse;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionItemRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionProveedorRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionRequest;
import um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.infrastructure.web.dto.CompraCotizacionResponse;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CompraCotizacionDtoMapperTest {

    private final CompraCotizacionDtoMapper mapper = new CompraCotizacionDtoMapper();

    @Test
    void toDomainMapeaCabeceraItemsYProveedores() {
        CompraCotizacionRequest request = new CompraCotizacionRequest(15, null, "obs", null,
                new BigDecimal("100.00"), 2, null, 38,
                List.of(new CompraCotizacionItemRequest(100, 1, new BigDecimal("3.00"))),
                List.of(new CompraCotizacionProveedorRequest(7, "p@x.com", null)));

        CompraCotizacion proceso = mapper.toDomain(request);

        assertThat(proceso.getEjercicioId()).isEqualTo(15);
        assertThat(proceso.getCantidadMinima()).isEqualTo(2);
        assertThat(proceso.getItems()).hasSize(1);
        assertThat(proceso.getItems().get(0).getCompraPedidoItemId()).isEqualTo(100);
        assertThat(proceso.getProveedores()).hasSize(1);
        assertThat(proceso.getProveedores().get(0).getEstado().name()).isEqualTo("PENDIENTE");
    }

    @Test
    void toDomainNuloDevuelveNulo() {
        assertThat(mapper.toDomain(null)).isNull();
    }

    @Test
    void toResponseMapeaElAgregado() {
        CompraCotizacion proceso = CompraCotizacion.builder()
                .compraCotizacionId(1L).numero("SC-1").ejercicioId(15)
                .estado(CompraCotizacionEstado.ENVIADA).build();
        proceso.setItems(List.of(CompraCotizacionItem.builder().compraCotizacionItemId(9L)
                .compraPedidoItemId(100).orden(1).cantidad(new BigDecimal("3.00")).build()));
        proceso.setProveedores(List.of(CompraCotizacionProveedor.builder()
                .compraCotizacionProveedorId(5L).proveedorId(7).estado(
                        um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionProveedorEstado.ENVIADA)
                .build()));

        CompraCotizacionResponse response = mapper.toResponse(proceso);

        assertThat(response.estado()).isEqualTo("ENVIADA");
        assertThat(response.items()).hasSize(1);
        assertThat(response.items().get(0).compraPedidoItemId()).isEqualTo(100);
        assertThat(response.proveedores()).hasSize(1);
        assertThat(response.proveedores().get(0).estado()).isEqualTo("ENVIADA");
    }

    @Test
    void toResponseNuloDevuelveNulo() {
        assertThat(mapper.toResponse((CompraCotizacion) null)).isNull();
        assertThat(mapper.toResponse((um.tesoreria.core.hexagonal.compras.pedidos.compraCotizacion.domain.model.CompraCotizacionPendiente) null)).isNull();
    }

    @Test
    void toResponseProveedorPendienteMapea() {
        CompraCotizacionProveedorPendiente pendiente = new CompraCotizacionProveedorPendiente(1L, "SC-1", 15, null,
                CompraCotizacionProveedor.builder().compraCotizacionProveedorId(5L).proveedorId(7)
                        .estado(CompraCotizacionProveedorEstado.ENVIADA).build(),
                List.of(CompraCotizacionItem.builder().compraCotizacionItemId(9L).compraPedidoItemId(100)
                        .orden(1).cantidad(new BigDecimal("3.00")).build()));

        var response = mapper.toResponse(pendiente);

        assertThat(response.numero()).isEqualTo("SC-1");
        assertThat(response.proveedor().proveedorId()).isEqualTo(7);
        assertThat(response.items()).hasSize(1);
        assertThat(mapper.toResponse((CompraCotizacionProveedorPendiente) null)).isNull();
    }

    @Test
    void toResponseAdjudicacionMapea() {
        CompraAdjudicacion adjudicacion = CompraAdjudicacion.builder()
                .compraAdjudicacionId(1L).compraCotizacionId(1L).ejercicioId(15)
                .compraCotizacionProveedorId(5L).proveedorAdjudicadoId(7).monto(new BigDecimal("28000.00"))
                .decision(CompraAdjudicacionDecision.ADJUDICADA).fundamento("menor total")
                .autoridadUsuarioId(38L).limiteAplicado(new BigDecimal("300000.00")).build();

        CompraAdjudicacionResponse response = mapper.toResponse(adjudicacion);

        assertThat(response.decision()).isEqualTo("ADJUDICADA");
        assertThat(response.proveedorAdjudicadoId()).isEqualTo(7);
        assertThat(response.monto()).isEqualByComparingTo("28000.00");
        assertThat(mapper.toResponse((CompraAdjudicacion) null)).isNull();
    }
}
