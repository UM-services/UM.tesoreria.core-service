package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.service.ProveedorMovimientoService;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoDetalle;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.ProveedorMovimientoResumen;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.AnulacionComprobanteResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoDetalleResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.ProveedorMovimientoResumenResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.mapper.ProveedorMovimientoDtoMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebMvcTest(ProveedorMovimientoController.class)
class ProveedorMovimientoControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private ProveedorMovimientoService service;

    @MockitoBean
    private ProveedorMovimientoDtoMapper dtoMapper;

    private static final String LISTA =
            "/api/tesoreria/core/proveedorMovimiento/proveedor/7/2026-01-01T00:00:00-03:00/2026-12-31T00:00:00-03:00/0";

    @Test
    void elListadoDevuelveLosMovimientosDelProveedor() {
        ProveedorMovimientoResumen resumen = ProveedorMovimientoResumen.builder()
                .proveedorMovimientoId(1L)
                .comprobanteDescripcion("Factura A")
                .numero("0001-00000010")
                .importe(new BigDecimal("100"))
                .cancelado(new BigDecimal("40"))
                .saldo(new BigDecimal("60"))
                .anulable(true)
                .motivosNoAnulable(List.of())
                .requierePrivilegioOp(false)
                .build();
        when(service.findAllByProveedorAndFechaRange(eq(7), any(OffsetDateTime.class),
                any(OffsetDateTime.class), eq(0))).thenReturn(List.of(resumen));
        when(dtoMapper.toResumen(resumen)).thenReturn(ProveedorMovimientoResumenResponse.builder()
                .proveedorMovimientoId(1L)
                .comprobanteDescripcion("Factura A")
                .numero("0001-00000010")
                .saldo(new BigDecimal("60"))
                .anulable(true)
                .requierePrivilegioOp(false)
                .build());

        mockMvc.get().uri(LISTA)
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$[0].proveedorMovimientoId").isEqualTo(1);
    }

    @Test
    void elDetalleDevuelveTipoYPagos() {
        ProveedorMovimientoDetalle detalle = ProveedorMovimientoDetalle.builder()
                .proveedorMovimientoId(1L)
                .tipo(ProveedorMovimientoDetalle.TIPO_FACTURA)
                .cabecera(ProveedorMovimientoResumen.builder().proveedorMovimientoId(1L).build())
                .anulacion(AnulacionComprobante.builder()
                        .habilitada(false)
                        .motivos(List.of(AnulacionComprobante.COSTOS_ASIGNADOS))
                        .requierePrivilegioOp(false)
                        .build())
                .build();
        when(service.findDetalle(1L)).thenReturn(detalle);
        when(dtoMapper.toDetalle(detalle)).thenReturn(ProveedorMovimientoDetalleResponse.builder()
                .proveedorMovimientoId(1L)
                .tipo(ProveedorMovimientoDetalle.TIPO_FACTURA)
                .cabecera(ProveedorMovimientoResumenResponse.builder().proveedorMovimientoId(1L).build())
                .pagos(List.of())
                .valores(List.of())
                .anulacion(AnulacionComprobanteResponse.builder()
                        .habilitada(false)
                        .motivos(List.of(AnulacionComprobante.COSTOS_ASIGNADOS))
                        .requierePrivilegioOp(false)
                        .build())
                .build());

        mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/1/detalle")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.tipo").isEqualTo("FACTURA");
    }

    @Test
    void unDetalleInexistenteDevuelve404() {
        when(service.findDetalle(99L)).thenThrow(new ProveedorMovimientoException(99L));

        mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/99/detalle")
                .assertThat().hasStatus(404);
    }
}
