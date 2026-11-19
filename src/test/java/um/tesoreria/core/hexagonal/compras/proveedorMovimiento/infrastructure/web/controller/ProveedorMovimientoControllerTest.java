package um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteEsOrdenPagoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ComprobanteNoImprimibleException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.CompraImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.OrdenPagoImpresion;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.CompraImpresionResponse;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.infrastructure.web.dto.OrdenPagoImpresionResponse;
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

    @Test
    void laImpresionDeUnaOrdenDePagoDevuelveLosTresBloques() {
        OrdenPagoImpresion impresion = OrdenPagoImpresion.builder().proveedorMovimientoId(10L).build();
        when(service.findOrdenPagoImpresion(10L)).thenReturn(impresion);
        when(dtoMapper.toImpresion(impresion)).thenReturn(OrdenPagoImpresionResponse.builder()
                .proveedorMovimientoId(10L)
                .numero("0015-00003684")
                .comprobantes(List.of())
                .valores(List.of())
                .traza(List.of(OrdenPagoImpresionResponse.Traza.builder()
                        .numeroCuenta(new BigDecimal("2110101"))
                        .nombreCuenta("Proveedores")
                        .debe(new BigDecimal("1000"))
                        .haber(BigDecimal.ZERO)
                        .build()))
                .build());

        var respuesta = mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/10/impresion");

        respuesta.assertThat().hasStatusOk().bodyJson().extractingPath("$.numero").isEqualTo("0015-00003684");
        respuesta.assertThat().bodyJson().extractingPath("$.traza[0].nombreCuenta").isEqualTo("Proveedores");
    }

    @Test
    void laImpresionDeUnComprobanteInexistenteDevuelve404() {
        when(service.findOrdenPagoImpresion(99L)).thenThrow(new ProveedorMovimientoException(99L));

        mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/99/impresion")
                .assertThat().hasStatus(404);
    }

    @Test
    void laImpresionDeUnComprobanteQueNoEsOrdenDePagoDevuelve409() {
        when(service.findOrdenPagoImpresion(5L)).thenThrow(new ComprobanteNoImprimibleException(5L));

        mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/5/impresion")
                .assertThat().hasStatus(409);
    }

    @Test
    void laImpresionDeUnaCompraDevuelveDetalleYAsiento() {
        CompraImpresion impresion = CompraImpresion.builder().proveedorMovimientoId(20L).build();
        when(service.findCompraImpresion(20L)).thenReturn(impresion);
        when(dtoMapper.toImpresionCompra(impresion)).thenReturn(CompraImpresionResponse.builder()
                .proveedorMovimientoId(20L)
                .numero("0007-00005682")
                .importe(new BigDecimal("-344825.51"))
                .detalle(List.of(CompraImpresionResponse.Detalle.builder().articulo("Mantenimiento Bienes de Uso").build()))
                .asiento(List.of(CompraImpresionResponse.Asiento.builder()
                        .numeroCuenta(new BigDecimal("20101090099"))
                        .nombreCuenta("Obligaciones a Pagar")
                        .build()))
                .build());

        var respuesta = mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/20/impresion-compra");

        respuesta.assertThat().hasStatusOk().bodyJson().extractingPath("$.numero").isEqualTo("0007-00005682");
        respuesta.assertThat().bodyJson().extractingPath("$.detalle[0].articulo").isEqualTo("Mantenimiento Bienes de Uso");
        respuesta.assertThat().bodyJson().extractingPath("$.asiento[0].nombreCuenta").isEqualTo("Obligaciones a Pagar");
    }

    @Test
    void laImpresionDeUnaCompraInexistenteDevuelve404() {
        when(service.findCompraImpresion(99L)).thenThrow(new ProveedorMovimientoException(99L));

        mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/99/impresion-compra")
                .assertThat().hasStatus(404);
    }

    @Test
    void laImpresionDeCompraDeUnaOrdenDePagoDevuelve409() {
        when(service.findCompraImpresion(10L)).thenThrow(new ComprobanteEsOrdenPagoException(10L));

        mockMvc.get().uri("/api/tesoreria/core/proveedorMovimiento/10/impresion-compra")
                .assertThat().hasStatus(409);
    }
}