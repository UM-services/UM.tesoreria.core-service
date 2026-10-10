package um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaDeudaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.model.ChequeraCuota;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.domain.ports.in.CalculateDeudaUseCase;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.dto.ChequeraCuotaResponse;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.infrastructure.web.mapper.ChequeraCuotaDtoMapper;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(ChequeraCuotaController.class)
class ChequeraCuotaControllerTest {

    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private ChequeraCuotaService service;
    @MockitoBean
    private ChequeraCuotaDeudaService deudaService;
    @MockitoBean
    private ChequeraSerieService serieService;
    @MockitoBean
    private CalculateDeudaUseCase calculateDeudaUseCase;
    @MockitoBean
    private ChequeraCuotaDtoMapper mapper;

    @Test
    void findAllByChequera_returnsBonifiedAndOriginalAmounts() throws Exception {
        var cuota = ChequeraCuota.builder().importe1(new BigDecimal("70")).importe1Original(new BigDecimal("100"))
                .importe2(new BigDecimal("140")).importe2Original(new BigDecimal("200"))
                .importe3(new BigDecimal("210")).importe3Original(new BigDecimal("300")).build();
        var response = ChequeraCuotaResponse.builder().importe1(cuota.getImporte1()).importe1Original(cuota.getImporte1Original())
                .importe2(cuota.getImporte2()).importe2Original(cuota.getImporte2Original())
                .importe3(cuota.getImporte3()).importe3Original(cuota.getImporte3Original()).build();
        when(service.findAllByFacultadIdAndTipoChequeraIdAndChequeraSerieIdAndAlternativaId(1, 2, 3L, 4))
                .thenReturn(List.of(cuota));
        when(mapper.toResponse(cuota)).thenReturn(response);

        var uri = "/api/tesoreria/core/chequeraCuota/chequera/1/2/3/4";
        assertJsonPath(uri, "$[0].importe1", 70);
        assertJsonPath(uri, "$[0].importe1Original", 100);
        assertJsonPath(uri, "$[0].importe2", 140);
        assertJsonPath(uri, "$[0].importe2Original", 200);
        assertJsonPath(uri, "$[0].importe3", 210);
        assertJsonPath(uri, "$[0].importe3Original", 300);
    }

    @Test
    void findAllInconsistencias_acceptsIsoDatesAndReturnsNoFalsePositives() throws Exception {
        var desde = OffsetDateTime.parse("2026-08-01T00:00:00Z");
        var hasta = OffsetDateTime.parse("2026-08-31T23:59:59Z");
        when(service.findAllInconsistencias(desde, hasta, false)).thenReturn(List.of());

        mockMvc.get().uri("/api/tesoreria/core/chequeraCuota/inconsistencias/2026-08-01T00:00:00Z/2026-08-31T23:59:59Z")
                .assertThat().hasStatusOk();
    }

    @Test
    void findByUnique_serializesBonifiedAmountsAndIsoDueDates() throws Exception {
        var cuota = ChequeraCuota.builder().build();
        var response = ChequeraCuotaResponse.builder()
                .importe1(new BigDecimal("70")).importe1Original(new BigDecimal("100"))
                .vencimiento1(OffsetDateTime.parse("2026-08-30T00:00:00Z"))
                .build();
        when(service.findByUnique(1, 2, 3L, 4, 5, 6)).thenReturn(cuota);
        when(mapper.toResponse(cuota)).thenReturn(response);

        var uri = "/api/tesoreria/core/chequeraCuota/unique/1/2/3/4/5/6";
        assertJsonPath(uri, "$.importe1", 70);
        assertJsonPath(uri, "$.importe1Original", 100);
        assertJsonPath(uri, "$.vencimiento1", "2026-08-30T00:00:00Z");
    }

    private void assertJsonPath(String uri, String path, Object expected) {
        mockMvc.get().uri(uri)
                .assertThat()
                .hasStatusOk()
                .bodyJson().extractingPath(path).isEqualTo(expected);
    }
}
