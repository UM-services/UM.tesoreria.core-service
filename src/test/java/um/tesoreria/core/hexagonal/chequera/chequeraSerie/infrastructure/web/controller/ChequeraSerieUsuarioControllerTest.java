package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequerasPorUsuarioService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.domain.model.ChequeraSerie;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.mapper.ChequeraSerieDtoMapper;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(ChequeraSerieController.class)
class ChequeraSerieUsuarioControllerTest {

    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private ChequeraSerieService chequeraSerieService;
    @MockitoBean
    private ChequeraCuotaService chequeraCuotaService;
    @MockitoBean
    private ChequeraSerieDtoMapper chequeraSerieDtoMapper;
    @MockitoBean
    private ChequerasPorUsuarioService chequerasPorUsuarioService;

    @Test
    void exposesPaginatedDebtStatusForAssignedUser() {
        var chequera = ChequeraSerie.builder()
                .chequeraId(10L)
                .facultadId(2)
                .lectivoId(2026)
                .importeDeuda(new BigDecimal("120.50"))
                .cuotasDeuda(2)
                .build();
        when(chequerasPorUsuarioService.findAll(7L, 2026, null, null, 0, 20))
                .thenReturn(new PageImpl<>(List.of(chequera), PageRequest.of(0, 20), 1));

        var response = mockMvc.get().uri("/api/tesoreria/core/chequeraSerie/usuario/7/lectivo/2026")
                .accept(MediaType.APPLICATION_JSON)
                .assertThat()
                .hasStatusOk();
        response.bodyJson().extractingPath("$.content[0].estadoDeuda").isEqualTo("CON_DEUDA_VENCIDA");
        response.bodyJson().extractingPath("$.totalElements").isEqualTo(1);
        response.bodyJson().extractingPath("$.number").isEqualTo(0);
        response.bodyJson().extractingPath("$.size").isEqualTo(20);
        response.bodyJson().extractingPath("$.totalPages").isEqualTo(1);
    }

    @Test
    void rejectsPartialStudentFilter() {
        when(chequerasPorUsuarioService.findAll(7L, 2026, new BigDecimal("123"), null, 0, 20))
                .thenThrow(new ChequerasPorUsuarioService.InvalidQueryException());

        mockMvc.get().uri("/api/tesoreria/core/chequeraSerie/usuario/7/lectivo/2026?personaId=123")
                .assertThat()
                .hasStatus(400);
    }
}
