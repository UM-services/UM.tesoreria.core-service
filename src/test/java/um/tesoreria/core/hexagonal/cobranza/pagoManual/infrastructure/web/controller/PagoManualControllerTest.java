package um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception.PagoManualException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.application.exception.PagoManualNotFoundException;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.model.PagoManual;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in.EliminarPagoManualUseCase;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.domain.ports.in.RegistrarPagoManualUseCase;
import um.tesoreria.core.hexagonal.cobranza.pagoManual.infrastructure.web.mapper.PagoManualDtoMapper;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(PagoManualController.class)
@Import(PagoManualDtoMapper.class)
class PagoManualControllerTest {

    private static final String BASE = "/api/tesoreria/core/cobranza/pago-manual";

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private RegistrarPagoManualUseCase registrarPagoManualUseCase;
    @MockitoBean
    private EliminarPagoManualUseCase eliminarPagoManualUseCase;

    @Test
    void registraUnPagoManual() {
        when(registrarPagoManualUseCase.registrar(any())).thenReturn(new PagoManual(
                77L, 99L, 6, 2, 123L, 1, 1, 1,
                OffsetDateTime.parse("2026-03-15T00:00:00Z"), new BigDecimal("100.00"),
                "Pago en efectivo", 2));

        var respuesta = mockMvc.post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"chequeraCuotaId\":99,\"fecha\":\"2026-03-15\","
                        + "\"importe\":100,\"observaciones\":\"Pago en efectivo\"}")
                .exchange();

        assertThat(respuesta).hasStatusOk();
        assertThat(respuesta).bodyJson().extractingPath("$.chequeraPagoId").isEqualTo(77);
        assertThat(respuesta).bodyJson().extractingPath("$.tipoPagoId").isEqualTo(2);
    }

    @Test
    void importeMayorAlSaldoDevuelve400() {
        when(registrarPagoManualUseCase.registrar(any()))
                .thenThrow(new PagoManualException("El importe supera el saldo de la cuota"));

        var respuesta = mockMvc.post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"chequeraCuotaId\":99,\"fecha\":\"2026-03-15\",\"importe\":150}")
                .exchange();

        assertThat(respuesta).hasStatus(400);
    }

    @Test
    void cuotaPagadaDevuelve409() {
        when(registrarPagoManualUseCase.registrar(any()))
                .thenThrow(new PagoManualException("La cuota ya está pagada", true));

        var respuesta = mockMvc.post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"chequeraCuotaId\":99,\"fecha\":\"2026-03-15\",\"importe\":10}")
                .exchange();

        assertThat(respuesta).hasStatus(409);
    }

    @Test
    void cuotaInexistenteDevuelve404() {
        when(registrarPagoManualUseCase.registrar(any()))
                .thenThrow(new PagoManualNotFoundException("No existe la cuota 99"));

        var respuesta = mockMvc.post().uri(BASE)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"chequeraCuotaId\":99,\"fecha\":\"2026-03-15\",\"importe\":10}")
                .exchange();

        assertThat(respuesta).hasStatus(404);
    }

    @Test
    void eliminaUnPagoManual() {
        var respuesta = mockMvc.delete().uri(BASE + "/77").exchange();

        assertThat(respuesta).hasStatus(204);
        verify(eliminarPagoManualUseCase).eliminar(77L);
    }

    @Test
    void noEliminaUnPagoNoManual() {
        when(eliminarPagoManualUseCase.eliminar(77L))
                .thenThrow(new PagoManualException("Sólo se pueden eliminar pagos manuales", true));

        var respuesta = mockMvc.delete().uri(BASE + "/77").exchange();

        assertThat(respuesta).hasStatus(409);
    }

}
