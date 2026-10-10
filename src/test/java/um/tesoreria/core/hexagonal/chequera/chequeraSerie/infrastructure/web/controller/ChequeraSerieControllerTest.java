package um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.hexagonal.chequera.chequeraCuota.application.service.ChequeraCuotaService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.application.service.ChequeraSerieService;
import um.tesoreria.core.hexagonal.chequera.chequeraSerie.infrastructure.web.mapper.ChequeraSerieDtoMapper;

@WebMvcTest(ChequeraSerieController.class)
class ChequeraSerieControllerTest {

    @Autowired
    private MockMvcTester mockMvc;
    @MockitoBean
    private ChequeraSerieService service;
    @MockitoBean
    private ChequeraCuotaService chequeraCuotaService;
    @MockitoBean
    private ChequeraSerieDtoMapper mapper;

    @Test
    void update_rejectsBenefitOutsideTheFractionalScaleBeforeMapping() {
        mockMvc.put().uri("/api/tesoreria/core/chequeraSerie/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"becaPorcentaje\":50}")
                .assertThat().hasStatus(400);
    }
}
