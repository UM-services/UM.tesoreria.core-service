package um.tesoreria.core.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.service.PlanService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(PlanController.class)
class PlanControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private PlanService service;

    @Test
    void findAllByFacultadConRutaCanonica() {
        when(service.findAllByFacultadId(1)).thenReturn(List.of());

        mockMvc.get().uri("/plan/facultad/1")
                .assertThat().hasStatusOk();
    }

    @Test
    void findAllByFacultadConAliasLegacyDeGestion() {
        when(service.findAllByFacultadId(1)).thenReturn(List.of());

        mockMvc.get().uri("/plan/1")
                .assertThat().hasStatusOk();
    }
}
