package um.tesoreria.core.controller;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import um.tesoreria.core.model.Provincia;
import um.tesoreria.core.service.ProvinciaService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(ProvinciaController.class)
class ProvinciaControllerTest {

    @Autowired
    private MockMvcTester mockMvc;

    @MockitoBean
    private ProvinciaService service;

    @Test
    void altaConElBodyQueMandaGestion() {
        when(service.add(any())).thenAnswer(inv -> {
            Provincia provincia = inv.getArgument(0);
            provincia.setUniqueId(1L);
            return provincia;
        });

        mockMvc.post().uri("/api/tesoreria/core/provincia/")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"facultadId":10,"provinciaId":5,"nombre":"Buenos Aires"}
                        """)
                .assertThat().hasStatusOk()
                .bodyJson().extractingPath("$.nombre").isEqualTo("Buenos Aires");

        ArgumentCaptor<Provincia> captor = ArgumentCaptor.forClass(Provincia.class);
        verify(service).add(captor.capture());
        assertThat(captor.getValue().getFacultadId()).isEqualTo(10);
        assertThat(captor.getValue().getProvinciaId()).isEqualTo(5);
    }
}
