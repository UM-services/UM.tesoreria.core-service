package um.tesoreria.core.controller.facade;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.exception.ComprobanteAnulacionException;
import um.tesoreria.core.exception.PermisoInsuficienteException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.application.exception.ProveedorMovimientoException;
import um.tesoreria.core.hexagonal.compras.proveedorMovimiento.domain.model.AnulacionComprobante;
import um.tesoreria.core.service.facade.CompraService;

import java.util.List;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@WebMvcTest(CompraController.class)
class CompraControllerTest {

    private static final String ANULACION = "/compra/comprobante/123";

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private CompraService service;

    @Test
    void anularDevuelveOk() {
        mockMvc.delete().uri(ANULACION).header("X-User-Id", "5")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.anulado").isEqualTo(true);

        verify(service).anularComprobante(123L, 5L);
    }

    @Test
    void anularRespondeTambienPorLaRutaLargaDelModulo() {
        mockMvc.delete().uri("/api/tesoreria/core/compra/comprobante/123").header("X-User-Id", "5")
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.anulado").isEqualTo(true);

        verify(service).anularComprobante(123L, 5L);
    }

    @Test
    void unaReglaIncumplidaDevuelve409ConLosMotivos() {
        doThrow(new ComprobanteAnulacionException(List.of(AnulacionComprobante.COSTOS_ASIGNADOS)))
                .when(service).anularComprobante(123L, 5L);

        mockMvc.delete().uri(ANULACION).header("X-User-Id", "5")
                .assertThat().hasStatus(409)
                .bodyJson().extractingPath("$.motivos[0]").isEqualTo("COSTOS_ASIGNADOS");
    }

    @Test
    void sinPrivilegioDeOrdenDePagoDevuelve403() {
        doThrow(new PermisoInsuficienteException(CompraService.PERMISO_ANULAR_ORDEN_PAGO))
                .when(service).anularComprobante(123L, 5L);

        mockMvc.delete().uri(ANULACION).header("X-User-Id", "5")
                .assertThat().hasStatus(403);
    }

    @Test
    void unComprobanteInexistenteDevuelve404() {
        doThrow(new ProveedorMovimientoException(123L))
                .when(service).anularComprobante(123L, 5L);

        mockMvc.delete().uri(ANULACION).header("X-User-Id", "5")
                .assertThat().hasStatus(404);
    }

    @Test
    void unErrorDeLaCascadaDevuelve406() {
        doThrow(new IllegalStateException("asiento contable"))
                .when(service).anularComprobante(123L, 5L);

        mockMvc.delete().uri(ANULACION).header("X-User-Id", "5")
                .assertThat().hasStatus(406);
    }
}
