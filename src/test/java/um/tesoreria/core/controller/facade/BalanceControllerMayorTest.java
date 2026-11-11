package um.tesoreria.core.controller.facade;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import um.tesoreria.core.service.facade.BalanceService;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException;
import um.tesoreria.core.service.facade.mayor.MayorConsultaException.Codigo;
import um.tesoreria.core.service.facade.mayor.MayorConsultaRequest;
import um.tesoreria.core.service.facade.mayor.MayorConsultaResponse;
import um.tesoreria.core.service.facade.mayor.MayorConsultaService;
import um.tesoreria.core.service.facade.mayor.MayorCuentaResponse;
import um.tesoreria.core.service.facade.mayor.MayorSaldoInicialResponse;

@WebMvcTest(BalanceController.class)
class BalanceControllerMayorTest {

    private static final String CUERPO = """
            {"desde":"2026-04-01T00:00:00Z","hasta":"2026-06-30T00:00:00Z","cuentas":[10101010001]}""";

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private BalanceService balanceService;

    @MockitoBean
    private MayorConsultaService mayorConsultaService;

    // protects=la consulta responde por la ruta corta y por la del gateway con el mismo cuerpo; fails_when=falta el alias /api/tesoreria/core/balance y el gateway devuelve 404; no_claim=no prueba el enrutamiento real del gateway
    @Test
    void laConsultaRespondePorLasDosRutas() {
        MayorConsultaResponse respuesta = new MayorConsultaResponse(null, null, List.of(new MayorCuentaResponse(
                new BigDecimal("10101010001"), "Caja",
                new MayorSaldoInicialResponse(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO),
                List.of(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO)));
        when(mayorConsultaService.consultar(any(MayorConsultaRequest.class))).thenReturn(respuesta);

        for (String ruta : List.of("/balance/mayor/consulta", "/api/tesoreria/core/balance/mayor/consulta")) {
            mockMvc.post().uri(ruta).contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                    .assertThat().hasStatusOk()
                    .bodyJson().extractingPath("$.cuentas[0].nombre").isEqualTo("Caja");
        }
    }

    // protects=un rechazo de negocio sale con su estado y el cuerpo {status, error, codigo, message}; fails_when=sale un 500 sin cuerpo o falta el codigo; no_claim=no prueba qué reglas generan cada código (lo cubre el test del servicio)
    @Test
    void unRechazoDeNegocioSaleConCodigoYMensaje() {
        when(mayorConsultaService.consultar(any(MayorConsultaRequest.class))).thenThrow(
                new MayorConsultaException(Codigo.EJERCICIOS_NO_CONSECUTIVOS, "falta un ejercicio"));

        for (String campo : List.of("status", "codigo", "message")) {
            Object esperado = switch (campo) {
                case "status" -> 409;
                case "codigo" -> "EJERCICIOS_NO_CONSECUTIVOS";
                default -> "falta un ejercicio";
            };
            mockMvc.post().uri("/api/tesoreria/core/balance/mayor/consulta")
                    .contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                    .assertThat().hasStatus(409)
                    .bodyJson().extractingPath("$." + campo).isEqualTo(esperado);
        }
    }

    // protects=una cuenta inexistente responde 404; fails_when=se responde 409 o 500; no_claim=no prueba el texto del mensaje
    @Test
    void unaCuentaInexistenteResponde404() {
        when(mayorConsultaService.consultar(any(MayorConsultaRequest.class))).thenThrow(
                new MayorConsultaException(Codigo.CUENTA_INEXISTENTE, "No existe la cuenta 1"));

        mockMvc.post().uri("/balance/mayor/consulta")
                .contentType(MediaType.APPLICATION_JSON).content(CUERPO)
                .assertThat().hasStatus(404);
    }
}