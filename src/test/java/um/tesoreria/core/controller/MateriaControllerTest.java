package um.tesoreria.core.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.service.MateriaService;

import java.util.List;

import static org.mockito.Mockito.when;

@WebMvcTest(MateriaController.class)
class MateriaControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private MateriaService service;

    @Test
    void findAllByFacultadConRutaCanonica() {
        when(service.findAllByFacultadId(1)).thenReturn(List.of());

        mockMvc.get().uri("/materia/facultad/1")
                .assertThat().hasStatusOk();
    }

    @Test
    void findAllByFacultadConAliasLegacyDeGestion() {
        when(service.findAllByFacultadId(1)).thenReturn(List.of());

        mockMvc.get().uri("/materia/1")
                .assertThat().hasStatusOk();
    }
}
